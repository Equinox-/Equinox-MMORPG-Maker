package com.pi.server.net.client;

import java.net.Socket;
import java.util.Vector;

import com.pi.common.debug.PILogger;
import com.pi.common.net.client.ClientPacket;
import com.pi.common.net.client.NetClient;
import com.pi.common.net.client.NetProcessingThread;
import com.pi.server.Server;

public class NetServerClient extends NetClient {
	private final Server server;
	private final NetProcessingThread procThread;

	public NetServerClient(Server server, NetProcessingThread procThread, Socket sock) {
		this.server = server;
		this.procThread = procThread;
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

	@Override
	protected Vector<ClientPacket> getLowProcessQueue() {
		return procThread.lowQueue;
	}

	@Override
	protected Vector<ClientPacket> getHighProcessQueue() {
		return procThread.highQueue;
	}

}
