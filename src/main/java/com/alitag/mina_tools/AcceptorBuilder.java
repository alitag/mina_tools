package com.alitag.mina_tools;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import com.alitag.mina_tools.filters.LoggingFilter;

/**
 * <p>
 * 该类用于快速构造一个IoAcceptor。它可以使用默认的MinaConfig值或另外指定各参数。
 * </p>
 * <p>
 * 在默认的情况下，生成的IoAcceptor有以下特性：
 * <ul>
 * <li>1. 使用基于文本的编码解码器，且其参数如字符集、行分隔符见MinaConfig类的默认值
 * <li>2. 使用LoggingFilter来记录各种交互事件
 * <li>3. 使用线程池
 * </ul>
 * </p>
 * <p>
 * <b>线程安全</b> 该类线程安全，因为已经做了合适的同步处理
 * </p>
 * 
 * @author gchangyi
 * @version 1.0
 * 
 */
public class AcceptorBuilder {

	/** 用于持有各种参数，初始值为null，将在构造函数中被初始化。 */
	private final MinaConfig config;

	/** 用于持有将会生成的IoAcceptor对象 */
	private IoAcceptor acceptor;

	/** 将被acceptor对象使用的线程池.NOTE:这里说是线程"池",但是目前只允许它里面有一条线程运行. */
	private ThreadPoolExecutor threadPool;

	/** 用于唯一确定codecFilter的key */
	public static final String FILTER_CODEC = AcceptorBuilder.class.getSimpleName() + ".codec";
	/** 用于唯一确定logginFilter的key */
	public static final String FILTER_LOGGING = AcceptorBuilder.class.getSimpleName() + ".logging";
	/** 用于唯一确定threadPool的key */
	public static final String FILTER_THREADPOOL = AcceptorBuilder.class.getSimpleName() + ".threadPool";

	/**
	 * <p>
	 * 默认构造函数。将产生一个MinaConfig对象并使用其默认值。
	 * </p>
	 * 
	 * @see MinaConfig
	 */
	public AcceptorBuilder() {
		config = new MinaConfig();
	}

	/**
	 * <p>
	 * 构造函数。将使用指定的MinaConfig中的参数。
	 * </p>
	 * 
	 * @param config
	 *            将会使用的参数
	 * @throws IllegalArgumentException
	 *             如果config为null，或config.codecFactory为null
	 */
	public AcceptorBuilder(MinaConfig config) {
		ArgumentValidator.notNull(config, "config");
		ArgumentValidator.notNull(config.codecFactory, "config.codecFactory");
		this.config = config;
	}

	/**
	 * <p>
	 * 得到生成的IoAcceptor对象
	 * </p>
	 * 
	 * @return 生成的IoAcceptor对象
	 */
	public synchronized IoAcceptor getAcceptor() {
		if (acceptor == null) {

			// 使用的线程数比电脑的cpu多一个，可以增加性能
			acceptor = new SocketAcceptor(Runtime.getRuntime().availableProcessors() + 1, Executors
					.newCachedThreadPool());
			SocketAcceptorConfig serviceConfig = (SocketAcceptorConfig) acceptor.getDefaultConfig();

			serviceConfig.getSessionConfig().setReuseAddress(config.reuseAddress);
			serviceConfig.getSessionConfig().setKeepAlive(config.socket_keepAlive);
			serviceConfig.getSessionConfig().setSendBufferSize(config.send_buffer_size);
			serviceConfig.getSessionConfig().setTcpNoDelay(config.tcp_no_delay);

			// 如果soLinger为0，当连接断开后，可以很快重用该端口
			serviceConfig.getSessionConfig().setSoLinger(config.socket_soLinger);

			// BugFix: setResueAddress -> false
			// 如果这里设为true，当两个IoAcceptor监听同一个端口时，第一个IoAcceptor会监听到该端口，第二个会等待。直到第一个不再监听时，第二个才会接着监听。
			// 如果设为false，第二个IoAcceptor会直接抛出异常
			serviceConfig.setReuseAddress(false);
			serviceConfig.setThreadModel(ThreadModel.MANUAL);

			// 加入codec
			serviceConfig.getFilterChain().addLast(FILTER_CODEC, new ProtocolCodecFilter(config.codecFactory));

			// 是否启用log
			if (config.log) {
				serviceConfig.getFilterChain().addLast(FILTER_LOGGING, createLoggingFilter());
			}

			// 是否启用线程池
			if (config.threadPool) {
				// NOTE: maxPoolSize由Integer.MAX_VALUE改为1
				// 经研究发现，maxPoolSize仅当blockingQueue放不下新的任务需要创建新线程时，才需要比较maxPoolSize的值
				// 当创建了新的线程后，提交的任务将会有多个同时执行
				// 当使用new LinkedBlockingQueue<Runnable>()时，它的容量为Integer.MAX_VALUE，所以通常不会发生放不下新任务的情况
				// 另:因为我们发送信息都是按顺序发送，所以我们必须保证只能有一个线程在运行,所以需要设置:
				// corePoolSize = 1, maxPoolSize = 1
				threadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
				// 当线程池关闭时，忽略因此产生的RejectedExecutionException
				threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
				serviceConfig.getFilterChain().addLast(FILTER_THREADPOOL, new ExecutorFilter(threadPool));
			}
		}
		return acceptor;
	}

	/**
	 * <p>
	 * 关闭线程池。如果没有启用或者已经关闭，不会有任何影响。
	 * </p>
	 * 
	 * @see ThreadPoolExecutor#shutdown()
	 */
	public synchronized void shutdownThreadPool() {
		if (threadPool != null) {
			threadPool.shutdown();
			threadPool = null;
		}
	}

	/**
	 * <p>
	 * 立刻关闭线程池。池中未运行的任务将会被取消。
	 * </p>
	 * 
	 * @see ThreadPoolExecutor#shutdownNow()
	 */
	public synchronized void shutdownThreadPoolNow() {
		if (threadPool != null) {
			threadPool.shutdownNow();
			threadPool = null;
		}
	}

	/**
	 * 检查线程池是否被关闭。如果线程池没有开启或者已经关闭，则返回true。
	 */
	public synchronized boolean threadPoolIsDisabledOrTerminated() {
		return threadPool == null || threadPool.isTerminated();
	}

	/**
	 * <p>
	 * 得到持有的MinaConfig对象。对于该config的修改不会对已经生成的IoAcceptor对象产生影响。
	 * </p>
	 * 
	 * @return 得到持有的MinaConfig对象
	 */
	public MinaConfig getMinaConfig() {
		return config;
	}

	public LoggingFilter createLoggingFilter() {
		int logWidth = Math.max(config.logWidth, 0);
		return new LoggingFilter(logWidth, config.logReceived, config.logWritten, config.logSent);
	}
}
