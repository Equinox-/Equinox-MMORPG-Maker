package com.pi.client.net;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import com.pi.client.Client;
import com.pi.common.PILogger;
import com.pi.common.net.client.NetClient;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;

public class NetClientClient extends NetClient {
    private final Client client;

    public NetClientClient(Client client) {
	this.client = client;
    }

    public boolean connect(String ip, int port) {
	dispose();
	try {
	    Socket sock = new Socket(ip, port);
	    connect(0, sock, new NetClientHandler(this, client));
	    return true;
	} catch (ConnectException e) {
	    return false;
	} catch (IOException e) {
	    e.printStackTrace();
	    return false;
	} catch (SecurityException e) {
	    e.printStackTrace();
	    return false;
	}
    }

    @Override
    public void dispose() {
	if (sock != null && sock.isConnected() && dOut != null
		&& !sock.isOutputShutdown()) {
	    try {
		Packet p = new Packet0Disconnect("", "");
		p.writePacket(dOut);
		dOut.flush();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	super.dispose();
    }

    @Override
    public void dispose(String reason, String details) {
	if (sock != null && sock.isConnected() && dOut != null
		&& !sock.isOutputShutdown()) {
	    try {
		Packet p = new Packet0Disconnect(reason, details);
		p.writePacket(dOut);
		dOut.flush();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
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
