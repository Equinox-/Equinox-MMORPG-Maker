package com.pi.server.net.client;

import java.net.Socket;

import com.pi.common.debug.PILogger;
import com.pi.common.net.client.NetClient;
import com.pi.server.Server;

public class NetServerClient extends NetClient {
	private final Server server;

	public NetServerClient(Server server, Socket sock) {
		this.server = server;
		connect(sock, new NetServerHandler(server, this));
	}

	@Override
	public PILogger getLog() {
		return server.getLog();
	}

	@Override
	public void dispose() {
		super.dispose();
		server.getClientManager().disposeClient(getID());
	}

	@Override
	public ThreadGroup getThreadGroup() {
		return server.getThreadGroup();
	}

}
