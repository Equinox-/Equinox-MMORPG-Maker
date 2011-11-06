package com.pi.client.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import com.pi.client.Client;
import com.pi.common.debug.PILogger;
import com.pi.common.net.client.NetClient;

public class NetClientClient extends NetClient {
    private final Client client;

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

    @Override
    public PILogger getLog() {
	return client.getLog();
    }

    @Override
    public ThreadGroup getThreadGroup() {
	return client.getThreadGroup();
    }
}
