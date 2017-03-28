package com.alitag.mina_tools;

import java.util.Timer;
import java.util.UUID;

import org.apache.mina.common.IoFuture;
import org.apache.mina.common.IoFutureListener;
import org.apache.mina.common.IoSession;

/**
 * IoSession的辅助类,用于向session中加入一些自动执行的任务
 * <p>
 * 线程安全：该类线程安全。因为它是不可变类。
 * 
 * @author gchangyi
 * @version 1.0
 */
public class SessionTaskHelper {

	private static final String PREFIX = SessionTaskHelper.class.getName();

	private static final String KEY_CANCEL_AUTODISCONNECT = PREFIX + ".cancel_autodisconnect";

	/**
	 * 私有构造函数.防止实例化.
	 */
	private SessionTaskHelper() {
		// do nothing
	}

	/**
	 * 设定该session在指定的时间后自动断开.如果session处于关闭状态,则不进行操作
	 * 
	 * @param session
	 *            欲断开的session
	 * @param seconds
	 *            多少秒后断开
	 * @throws IllegalArgumentException
	 *             如果session为null,或者seconds<0
	 */
	public static void setAutoDisconnect(final IoSession session, final int seconds) {
		ArgumentValidator.notNull(session, "session");
		ArgumentValidator.isTrue(seconds >= 0, "seconds should be >=0: " + seconds);
		if (session.isClosing())
			return;

		TimerTaskExt task = new TimerTaskExt() {
			@Override
			public void run() {
				if (session.containsAttribute(KEY_CANCEL_AUTODISCONNECT)) {
					session.removeAttribute(KEY_CANCEL_AUTODISCONNECT);
				} else {
					session.close();
				}
				if (this.getOwner() != null)
					this.getOwner().cancel();
			}

			@Override
			public String getName() {
				return "auto disconnect after " + seconds + "s";
			}
		};
		addAutoCancelTask(session, task, seconds * 1000, 0);
	}

	/**
	 * 取消通过{@link #setAutoDisconnect()}设置的自动断开任务
	 * 
	 * @param session
	 *            欲取消断开任务的连接
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static void cancelAutoDisconnect(final IoSession session) {
		ArgumentValidator.notNull(session, "session");
		session.setAttribute(KEY_CANCEL_AUTODISCONNECT);
	}

	/**
	 * 增加一个在session关闭时会自动取消的任务.可以设置为延时多久后执行,执行一次或每隔一段时间反复执行
	 * 
	 * @param session
	 *            当前的连接对象
	 * @param task
	 *            要运行的任务
	 * @param delayMillis
	 *            多少毫秒后开始运行
	 * @param period
	 *            隔多久运行一次.如果为0,表示只运行一次
	 * @throws IllegalArgumentException
	 *             如果session为null,或者task为null,或者delayMillis<0,或者period<0
	 */
	public static void addAutoCancelTask(final IoSession session, final TimerTaskExt task, long delayMillis, long period) {
		ArgumentValidator.notNull(session, "session");
		ArgumentValidator.notNull(task, "task");
		ArgumentValidator.isTrue(delayMillis >= 0, "delayMillis should be >=0: " + delayMillis);
		ArgumentValidator.isTrue(period >= 0, "period should be >=0: " + period);

		Timer timer = new Timer(task.getName());
		task.setOwner(timer);
		if (period > 0) {
			timer.schedule(task, delayMillis, period);
		} else {
			timer.schedule(task, delayMillis);
		}

		// 生成唯一id
		final String attrId = "auto cacel task: " + UUID.randomUUID().toString();
		session.setAttribute(attrId, timer);

		// session关闭时自动停止该timer
		session.getCloseFuture().addListener(new IoFutureListener() {
			public void operationComplete(IoFuture future) {
				Timer timer = (Timer) session.getAttribute(attrId);
				if (timer != null) {
					timer.cancel();
				}
			}
		});
	}

}
