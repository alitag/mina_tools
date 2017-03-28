package com.alitag.mina_tools;

import java.net.InetSocketAddress;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoConnector;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectorHelper {

	private static final Logger logger = LoggerFactory.getLogger(ConnectorHelper.class);

	private IoConnector connector;
	private IoHandler handler;

	private InetSocketAddress localPort;

	public ConnectorHelper(IoConnector connector, IoHandler handler) {
		this.connector = connector;
		this.handler = handler;
	}

	/**
	 * 连接到指定的ip及端口
	 */
	public IoSession connectTo(final String serverName, final String remoteIp, final int remotePort) {
		String remoteIpPort = "[/" + remoteIp + ": " + remotePort + "] ";

		try {
			ConnectFuture future = null;
			if (this.localPort != null) {
				logger.info(remoteIpPort + "Connecting...(local port: " + this.localPort + ")");
				future = this.connector.connect(new InetSocketAddress(remoteIp, remotePort), this.localPort,
						this.handler);
			} else {
				logger.info(remoteIpPort + "Connecting...");
				future = this.connector.connect(new InetSocketAddress(remoteIp, remotePort), this.handler);
			}

			future.join();
			IoSession session = future.getSession();

			return session;
		} catch (Exception e) {
			logger.warn(remoteIpPort + e.toString());
			return null;
		}
	}

	public InetSocketAddress getLocalPort() {
		return this.localPort;
	}

	public ConnectorHelper setLocalPort(InetSocketAddress localPort) {
		this.localPort = localPort;
		return this;
	}

}
