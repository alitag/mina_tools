package com.alitag.mina_tools;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.apache.mina.common.ExceptionMonitor;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

/**
 * <p>
 * 该类是一个配置信息类。我们可以在这个类中集中设置各种信息，用于生成对应的mina的IoAcceptor或者IoConnector
 * </p>
 * <p>
 * <b>线程安全</b> 该类非线程安全，因为它是可变类。
 * </p>
 * 
 * @author gchangyi
 * @version 1.0
 */
public class MinaConfig {

	private static int DEFAULT_RECEIVE_BUFFER_SIZE = 1024;
	private static int DEFAULT_SEND_BUFFER_SIZE = 1024;
	private static boolean TCP_NO_DELAY = false;
	static {
		Socket unconnectedSocket = new Socket();
		try {
			DEFAULT_RECEIVE_BUFFER_SIZE = unconnectedSocket.getReceiveBufferSize();
			DEFAULT_SEND_BUFFER_SIZE = unconnectedSocket.getSendBufferSize();
			TCP_NO_DELAY = unconnectedSocket.getTcpNoDelay();
		} catch (SocketException se) {
			ExceptionMonitor.getInstance().exceptionCaught(se);
			try {
				unconnectedSocket.close();
			} catch (IOException ioe) {
				ExceptionMonitor.getInstance().exceptionCaught(ioe);
			}
		}
	}

	/**
	 * <p>
	 * 是否启用log。默认启用。
	 * </p>
	 */
	public boolean log = true;

	/**
	 * <p>
	 * 每行log的长度，超过该长度的日志将被截断。0或负数表示不限制。默认不限制。
	 * </p>
	 */
	public int logWidth = 0;

	/** 是否记录收到的信息，默认为true */
	public boolean logReceived = true;

	/** 是否记录写入发送缓冲区的信息，默认为false */
	public boolean logWritten = false;

	/** 是否记录已经发送的信息，默认为true */
	public boolean logSent = true;

	/**
	 * <p>
	 * 是否启动线程池。默认启用。
	 * </p>
	 */
	public boolean threadPool = true;

	/**
	 * <p>
	 * 客户端连接到服务器时的超时时间，默认为1秒。仅对ConnectorBuilder有效。
	 * </p>
	 */
	public int connectTimeout = 1;

	/**
	 * <p>
	 * 使用的codecFactory。默认为null。当codecFactory为null时，将使用后面的textCodec_*来生成一个TextLineCodecFactory对象
	 * </p>
	 */
	public ProtocolCodecFactory codecFactory = new TextLineCodecFactory();

	/**
	 * <p>
	 * socket设置：是否重用地址（ip加端口），默认为true
	 * </p>
	 */
	public boolean reuseAddress = true;

	/**
	 * <p>
	 * socket设置：是否从socket级别保持连接，默认为true
	 * </p>
	 */
	public boolean socket_keepAlive = true;

	/**
	 * <p>
	 * socket设置：soLinger，默认为 0
	 * </p>
	 */
	public int socket_soLinger = 0;

	/**
	 * <p>
	 * 接收缓存大小设置，windows XP系统中默认值为8096字节，即4KB.
	 * </p>
	 */
	public int receiver_buffer_size = DEFAULT_RECEIVE_BUFFER_SIZE;

	/**
	 * <p>
	 * 发送缓存大小设置，windows XP系统中默认为8096字节，即8KB
	 * </p>
	 */
	public int send_buffer_size = DEFAULT_SEND_BUFFER_SIZE;

	/**
	 * <P>
	 * 是否不进行任何延迟就发送数据.Socket中默认设置为false。
	 * </P>
	 * 如果为true，则无论数据大小为多少，都会立刻发出；此时当数据小于TCP包头(40个Byte)大小时，容易产生过载现象，增加带宽占用。 <br>
	 * 如果为false，则当数据较小时（比如小于40个字节），会等到有更多待发送数据才封装成一个包一次性发出，最多会等待300ms。带宽占用减少但延迟增加
	 */
	public boolean tcp_no_delay = TCP_NO_DELAY;

	/**
	 * <p>
	 * 显示出当前的配置内容，格式为每行一个参数，每行形如：
	 * </p>
	 * <p>
	 * param: value
	 * </p>
	 * 
	 * @return 当前的配置内容
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("log: " + log).append(System.lineSeparator());
		sb.append("logWidth: " + logWidth).append(System.lineSeparator());
		sb.append("threadPool: " + threadPool).append(System.lineSeparator());
		sb.append("connectTimeout: " + connectTimeout).append(System.lineSeparator());
		sb.append("codecFacotry class: " + codecFactory.getClass().getName()).append(System.lineSeparator());
		sb.append("codecFactory content: " + codecFactory.toString()).append(System.lineSeparator());
		sb.append("socket_reuseAddress: " + reuseAddress).append(System.lineSeparator());
		sb.append("socket_keepAlive: " + socket_keepAlive).append(System.lineSeparator());
		sb.append("socket_soLinger: " + socket_soLinger).append(System.lineSeparator());
		sb.append("receiver_buffer_size: " + receiver_buffer_size).append(System.lineSeparator());
		sb.append("send_buffer_size: " + send_buffer_size).append(System.lineSeparator());
		sb.append("tcp_no_delay: " + tcp_no_delay).append(System.lineSeparator());
		return sb.toString();
	}

}
