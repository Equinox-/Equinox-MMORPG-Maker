package com.pi.client.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import com.pi.client.Client;
import com.pi.common.debug.PILogger;
import com.pi.common.net.NetHandler;
import com.pi.common.net.client.NetClient;

public class NetClientClient extends NetClient {
    private final Client client;
    private NetClientProcessingThread processingThread;

    public NetClientClient(Client client, String ip, int port) {
	this.client = client;
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
	    processingThread = new NetClientProcessingThread(this);
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

    public NetClientProcessingThread getNetProcessor() {
	return processingThread;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void forceDispose() {
	quitting = true;
	while (shouldProcessPacket()) {
	    try {
		Thread.sleep(100l);
	    } catch (InterruptedException e) {
		e.printStackTrace(getLog().getErrorStream());
	    }
	}
	if (getNetProcessor() != null && getNetProcessor().isAlive())
	    try {
		getNetProcessor().join();
	    } catch (Exception e) {
		e.printStackTrace(getLog().getErrorStream());
		getNetProcessor().stop();
	    }
	super.forceDispose();
    }

    @Override
    public PILogger getLog() {
	return client.getLog();
    }

    @Override
    public ThreadGroup getThreadGroup() {
	return client.getThreadGroup();
    }
}
