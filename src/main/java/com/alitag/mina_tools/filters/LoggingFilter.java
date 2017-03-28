package com.alitag.mina_tools.filters;

import java.io.IOException;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoFilterAdapter;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alitag.mina_tools.ArgumentValidator;
import com.alitag.mina_tools.SessionHelper;

/**
 * <p>
 * 该类是Mina的IoFilter的一个实现类，它可用于记录mina的服务器程序与客户端交互时的各种事件。与Mina自带的LoggingFilter相比，该类增加了限制每行日志最大长度的功能，在对性能要求较高的环境中比较有用。
 * </p>
 * <p>
 * 线程安全：该类线程安全，因为它是不可变类。
 * </p>
 * 
 * @author gchangyi
 * @version 1.0
 */
public class LoggingFilter extends IoFilterAdapter {

	/**
	 * <p>
	 * 用于记录日志的logger对象。
	 * </p>
	 */
	protected static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

	/**
	 * <p>
	 * 记录时，每行日志的最大长度。默认值为0，表示不限制。如果设置了一个正数，则超过该限制的多余文本将被截去并以...代替。
	 * </p>
	 */
	protected int maxWidth = 0;

	protected boolean loggingReceived = false;
	protected boolean loggingWritten = false;
	protected boolean loggingSent = false;

	/**
	 * <p>
	 * 默认构造函数。不限制每行日志的长度。
	 * </p>
	 */
	public LoggingFilter() {
		// do nothing
	}

	/**
	 * <p>
	 * 设置记录信息时，每行记录的最大的长度，超出部分将被截断。0表示不截断。
	 * </p>
	 * 
	 * @param maxWidth
	 *            每行日志的最大长度。须大于等于0。0表示不限制。
	 * @throws IllegalArgumentException
	 *             如果maxWidth小于0
	 */
	public LoggingFilter(int maxWidth) {
		this(maxWidth, true, false, true);
	}

	public LoggingFilter(int maxWidth, boolean loggingReceived, boolean loggingWritten, boolean loggingSent) {
		ArgumentValidator.isTrue(maxWidth >= 0, "maxWidth should >=0: " + maxWidth);
		this.maxWidth = maxWidth;
		this.loggingReceived = loggingReceived;
		this.loggingWritten = loggingWritten;
		this.loggingSent = loggingSent;
	}

	/**
	 * <p>
	 * 当新建一个连接时，记录下该事件，格式为：
	 * </p>
	 * <p>
	 * [/xxx.xxx.xxx.xxx: port] CREATED
	 * </p>
	 * <p>
	 * 该事件的日志不受maxWidth参数影响。
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @param nextFilter
	 *            filter链的下一个filter
	 * 
	 */
	@Override
	public void sessionCreated(NextFilter nextFilter, IoSession session) {
		if (logger.isInfoEnabled()) {
			logger.info(SessionHelper.getRemoteIpPort1(session) + " CREATED");
		}
		nextFilter.sessionCreated(session);
	}

	/**
	 * <p>
	 * 当打开一个连接时，记录下该事件，格式为：
	 * </p>
	 * <p>
	 * [/xxx.xxx.xxx.xxx: port] OPENED
	 * </p>
	 * <p>
	 * 该事件的日志不受maxWidth参数影响。
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @param nextFilter
	 *            filter链的下一个filter
	 * 
	 */
	@Override
	public void sessionOpened(NextFilter nextFilter, IoSession session) {
		if (logger.isInfoEnabled()) {
			logger.info(SessionHelper.getRemoteIpPort1(session) + " OPENED");
		}
		nextFilter.sessionOpened(session);
	}

	/**
	 * <p>
	 * 当连接关闭后，记录下该事件，格式为：
	 * </p>
	 * <p>
	 * [/xxx.xxx.xxx.xxx: port] CLOSED
	 * </p>
	 * <p>
	 * 该事件的日志不受maxWidth参数影响。
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @param nextFilter
	 *            filter链的下一个filter
	 * 
	 */
	@Override
	public void sessionClosed(NextFilter nextFilter, IoSession session) {
		if (logger.isInfoEnabled()) {
			logger.info(SessionHelper.getRemoteIpPort1(session) + " CLOSED");
		}
		nextFilter.sessionClosed(session);
	}

	/**
	 * <p>
	 * 当一个连接空闲时，记录下该事件，格式为：
	 * </p>
	 * <p>
	 * [/xxx.xxx.xxx.xxx: port] IDLE: status
	 * </p>
	 * <p>
	 * 该事件的日志不受maxWidth参数影响。
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @param status
	 *            当前的空闲状态
	 * @param nextFilter
	 *            filter链的下一个filter
	 * 
	 */
	@Override
	public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) {
		if (logger.isInfoEnabled()) {
			logger.info(SessionHelper.getRemoteIpPort1(session) + " IDLE: " + status);
		}
		nextFilter.sessionIdle(session, status);
	}

	/**
	 * <p>
	 * 当出现异常时，记录下该事件，格式为：
	 * </p>
	 * <p>
	 * [/xxx.xxx.xxx.xxx: port] 异常信息
	 * </p>
	 * <p>
	 * 如果cause是IOException类型，则记录错误信息。如果是其它类型的异常，则还要记录详细信息。
	 * </p>
	 * <p>
	 * 该事件的日志不受maxWidth参数影响。
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @param cause
	 *            发生的异常
	 * @param nextFilter
	 *            filter链的下一个filter
	 * 
	 */
	@Override
	public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) {
		if (cause instanceof IOException) {
			logger.error(SessionHelper.getRemoteIpPort1(session) + cause.toString());
		} else {
			logger.error(SessionHelper.getRemoteIpPort1(session), cause);
		}
		nextFilter.exceptionCaught(session, cause);
	}

	/**
	 * <p>
	 * 当收到信息时，记录下该事件，格式为：
	 * </p>
	 * <p>
	 * [/xxx.xxx.xxx.xxx: port] RECEIVED: 信息内容
	 * </p>
	 * <p>
	 * 信息内容的长度受到maxWidth的限制
	 * </p>
	 * 
	 * @param message
	 *            收到的信息对象
	 * @param session
	 *            当前的连接对象
	 * @param nextFilter
	 *            filter链的下一个filter
	 * 
	 */
	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) {
		if (loggingReceived && logger.isInfoEnabled()) {
			onLoggingMessageReceived(session, message);
		}
		nextFilter.messageReceived(session, message);
	}

	public void onLoggingMessageReceived(IoSession session, Object message) {
		logger.info(SessionHelper.getRemoteIpPort1(session) + " RECEIVED: " + abbreviate(message.toString()));
	}

	/**
	 * <p>
	 * 当信息发送后，记录下该事件，格式为：
	 * </p>
	 * <p>
	 * [/xxx.xxx.xxx.xxx: port] SENT: 信息内容
	 * </p>
	 * <p>
	 * 信息内容的长度受到maxWidth的限制
	 * </p>
	 * 
	 * @param message
	 *            发送的信息对象
	 * @param session
	 *            当前的连接对象
	 * @param nextFilter
	 *            filter链的下一个filter
	 * 
	 */
	@Override
	public void messageSent(NextFilter nextFilter, IoSession session, Object message) {
		if (loggingSent && logger.isInfoEnabled()) {
			onLoggingMessageSent(session, message);
		}
		nextFilter.messageSent(session, message);
	}

	public void onLoggingMessageSent(IoSession session, Object message) {
		logger.info(SessionHelper.getRemoteIpPort1(session) + " SENT: " + abbreviate(message.toString()));
	}

	/**
	 * <p>
	 * 当信息提交到mina的待发送列表后，记录下该事件，格式为：
	 * </p>
	 * <p>
	 * [/xxx.xxx.xxx.xxx: port] WRITE: 信息内容
	 * </p>
	 * <p>
	 * 信息内容的长度受到maxWidth的限制
	 * </p>
	 * 
	 * @param writeRequest
	 *            向mina待发送列表写入的对象
	 * @param session
	 *            当前的连接对象
	 * @param nextFilter
	 *            filter链的下一个filter
	 * 
	 */
	@Override
	public void filterWrite(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) {
		if (loggingWritten && logger.isInfoEnabled()) {
			onLoggingMessageWrite(session, writeRequest);
		}
		nextFilter.filterWrite(session, writeRequest);
	}

	public void onLoggingMessageWrite(IoSession session, WriteRequest writeRequest) {
		logger.info(SessionHelper.getRemoteIpPort1(session) + " WRITE: "
				+ abbreviate(writeRequest.getMessage().toString()));
	}

	/**
	 * <p>
	 * 当连接关闭时，记录下该事件，格式为：
	 * </p>
	 * <p>
	 * [/xxx.xxx.xxx.xxx: port] CLOSE
	 * </p>
	 * <p>
	 * 该事件的日志不受maxWidth参数影响。
	 * </p>
	 * 
	 * @param session
	 *            当前的连接对象
	 * @param nextFilter
	 *            filter链的下一个filter
	 * 
	 */
	@Override
	public void filterClose(NextFilter nextFilter, IoSession session) {
		if (logger.isInfoEnabled()) {
			logger.info(SessionHelper.getRemoteIpPort1(session) + " CLOSE");
		}
		nextFilter.filterClose(session);
	}

	/**
	 * <p>
	 * 得到当前设置的最大行宽。0表示没有限制。
	 * </p>
	 * 
	 * @return 当前设置的最大行宽。0表示没有限制。
	 */
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * <p>
	 * 如果maxWidth大于0，则将信息超过限制的部分截去。
	 * </p>
	 * 
	 * 示例： 假设当前maxWidth==6
	 * 
	 * <pre>
	 * null -&gt; null
	 * &quot;&quot; -&gt; &quot;&quot;
	 * 123 -&gt; 123
	 * 123456 -&gt; 123456
	 * 123456789 -&gt; 123...
	 * </pre>
	 * 
	 * @param message
	 *            欲处理的信息
	 * @return 处理后的信息
	 */
	protected String abbreviate(String message) {
		if (message == null)
			return null;
		return (maxWidth > 0 && message.length() > maxWidth) ? message.substring(0, maxWidth - 3) + "..." : message;
	}

	public boolean isLoggingReceived() {
		return loggingReceived;
	}

	public boolean isLoggingWritten() {
		return loggingWritten;
	}

	public boolean isLoggingSent() {
		return loggingSent;
	}

}
