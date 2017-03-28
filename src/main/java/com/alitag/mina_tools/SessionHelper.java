package com.alitag.mina_tools;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.common.IoSession;

/**
 * <p>
 * 与IoSession相关的工具类。
 * </p>
 * <p>
 * 线程安全：该类线程安全，因为它只提供了无状态的工具函数。
 * </p>
 *
 * @author gchangyi
 * @version 1.0
 */
public class SessionHelper {

	/**
	 * <p>
	 * 用于存取对方ip与端口的常量
	 * </p>
	 */
	private static final String KEY_PREFIX = SessionHelper.class.getName();

	/** 用于保存对方ip和port字符串的key */
	private static final String KEY_getIpPort = KEY_PREFIX + ".getIpPort";

	/**
	 * 私有构造函数。防止被实例化。
	 */
	private SessionHelper() {
		// do nothing
	}

	/**
	 * <p>
	 * 从session得到本机的InetSocketAddress.如果没得到,返回null
	 * </p>
	 *
	 * @param session
	 *            当前的连接对象
	 * @return 本机的InetAddress.如果没得到,返回null
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static InetSocketAddress getLocalAddress(IoSession session) {
		ArgumentValidator.notNull(session, "session");
		SocketAddress local = session.getLocalAddress();
		if (local != null && local instanceof InetSocketAddress) {
			return (InetSocketAddress) local;
		}
		return null;
	}

	/**
	 * <p>
	 * 得到本机ip.如果没得到,返回""
	 * </p>
	 *
	 * @param session
	 *            当前的连接对象
	 * @return 本机ip.如果没得到,返回""
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static String getLocalIp(IoSession session) {
		ArgumentValidator.notNull(session, "session");
		InetSocketAddress local = getLocalAddress(session);
		InetAddress address;
		if (local != null && ((address = local.getAddress()) instanceof Inet4Address)) {
			return ((Inet4Address) address).getHostAddress();
		}
		return "";
	}

	/**
	 * <p>
	 * 得到本机的端口.如果没得到,返回-1
	 * </p>
	 *
	 * @param session
	 *            当前的连接对象
	 * @return 本机的端口.如果没得到,返回-1
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static int getLocalPort(IoSession session) {
		ArgumentValidator.notNull(session, "session");
		SocketAddress address = session.getLocalAddress();
		if (address != null && address instanceof InetSocketAddress) {
			return ((InetSocketAddress) address).getPort();
		}
		return -1;
	}

	/**
	 * <p>
	 * 由session得到对方的InetAddress.如果没得到,返回null
	 * </p>
	 *
	 * @param session
	 *            当前的连接对象
	 * @return 对方的InetAddress.如果没得到,返回null
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static InetSocketAddress getRemoteAddress(IoSession session) {
		ArgumentValidator.notNull(session, "session");
		SocketAddress remote = session.getRemoteAddress();
		if (remote != null && remote instanceof InetSocketAddress) {
			return (InetSocketAddress) remote;
		}
		return null;
	}

	/**
	 * <p>
	 * 由session得到对方的IP地址.如果没得到,返回""
	 * </p>
	 *
	 * @param session
	 *            当前的连接对象
	 * @return 对方的IP地址.如果没得到,返回""
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static String getRemoteIp(IoSession session) {
		ArgumentValidator.notNull(session, "session");
		InetSocketAddress remote = getRemoteAddress(session);
		InetAddress address;
		if (remote != null && (address = remote.getAddress()) instanceof InetAddress) {
			return ((InetAddress) address).getHostAddress();
		}
		return "";

	}

	/**
	 * <p>
	 * 对session得到对方的Port.如果没得到,返回-1
	 * </p>
	 *
	 * @param session
	 *            当前的连接对象
	 * @return 对方的Port.如果没得到,返回-1
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static int getRemotePort(IoSession session) {
		ArgumentValidator.notNull(session, "session");
		SocketAddress remote = session.getRemoteAddress();
		if (remote != null && remote instanceof InetSocketAddress) {
			return ((InetSocketAddress) remote).getPort();
		}
		return -1;
	}

	/**
	 * <p>
	 * 得到一个session的ip和port，并以特定的格式返回一个字符串。格式为: [/xxx.xxx.xxx.xxx: port]
	 * </p>
	 * <p>
	 * 因为函数经常用于日志中,所以从性能方面考虑,在session已经连接成功后,会把对方的ip和port保存起来,以供以后直接使用.
	 * </p>
	 * <p>
	 * 如果没有得到对方的ip和port,返回的格式为[/: -1]
	 * </p>
	 *
	 * @param session
	 *            当前的连接对象
	 * @return 对方的ip和port,格式为[/xxx.xxx.xxx.xxx: port].如果没有得到对方的ip和port,返回的格式为[/: -1]
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static String getRemoteIpPort1(IoSession session) {
		ArgumentValidator.notNull(session, "session");
		String ipPort = (String) session.getAttribute(KEY_getIpPort);
		if (ipPort == null) {
			ipPort = "[/" + getRemoteIp(session) + ": " + getRemotePort(session) + "]";
			if (session.isConnected()) {
				session.setAttribute(KEY_getIpPort, ipPort);
			}
		}
		return ipPort;
	}

	/**
	 * <p>
	 * 由session得到对方的ip与port，由':'分隔,格式为xxx.xxx.xxx.xxx: port
	 * </p>
	 * <p>
	 * 如果没有得到对方的ip和port,返回的格式为: -1
	 * </p>
	 *
	 * @param session
	 *            当前的连接对象
	 * @return 得到对方的ip与port，由':'分隔,格式为xxx.xxx.xxx.xxx: port.没有得到对方的ip和port,返回的格式为: -1
	 * @throws IllegalArgumentException
	 *             如果session为null
	 */
	public static String getRemoteIpPort2(IoSession session) {
		ArgumentValidator.notNull(session, "session");
		return getRemoteIp(session) + ": " + getRemotePort(session);
	}
}
