package com.pi.client.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.channels.SocketChannel;

import com.pi.client.Client;
import com.pi.common.debug.PILogger;
import com.pi.common.net.NetHandler;
import com.pi.common.net.PacketHeap;
import com.pi.common.net.client.ClientPacket;
import com.pi.common.net.client.NetClient;
import com.pi.common.net.client.NetProcessingThread;

public class NetClientClient extends NetClient {
	private final Client client;
	private NetProcessingThread processingThread;

	public NetClientClient(Client client, String ip, int port) {
		this.client = client;
		processingThread = new NetProcessingThread(client.getThreadGroup(),
				"Network Processing Thread");
		try {
			Socket sock = new Socket(ip, port);
			connect(sock, new NetClientHandler(this, client));
		} catch (ConnectException e) {
			client.getLog().severe(e.toString());
		} catch (IOException e) {
			e.printStackTrace(client.getLog().getErrorStream());
		} catch (SecurityException e) {
			e.printStackTrace(client.getLog().getErrorStream());
		}
	}

	@Override
	public void connect(Socket sock, NetHandler netHandle) {
		super.connect(sock, netHandle);
		if (sock != null && sock.isConnected()) {
			processingThread.start();
		}
	}

	@Override
	public void dispose() {
		dispose("", "");
	}

	@Override
	public void dispose(String reason, String details) {
		/*
		 * if (sock != null && sock.isConnected() && dOut != null &&
		 * !sock.isOutputShutdown()) { try { Packet p = new
		 * Packet0Disconnect(reason, details); p.writePacket(dOut);
		 * dOut.flush(); } catch (Exception e) { e.printStackTrace(); } }
		 */
		super.dispose();
	}

	public NetProcessingThread getNetProcessor() {
		return processingThread;
	}

	@Override
	public void forceDispose() {
		quitting = true;
		super.forceDispose();
		getNetProcessor().dispose();
	}

	@Override
	public PILogger getLog() {
		return client.getLog();
	}

	@Override
	public ThreadGroup getThreadGroup() {
		return client.getThreadGroup();
	}

	@Override
	protected PacketHeap<ClientPacket> getLowProcessQueue() {
		return getNetProcessor().lowQueue;
	}

	@Override
	protected PacketHeap<ClientPacket> getHighProcessQueue() {
		return getNetProcessor().highQueue;
	}
}
