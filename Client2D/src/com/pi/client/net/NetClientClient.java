package com.pi.client.net;

import java.io.IOException;
import java.net.*;

import com.pi.client.Client;
import com.pi.common.PILogger;
import com.pi.common.net.client.NetClient;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;

public class NetClientClient extends NetClient {
    private final Client client;

    public NetClientClient(Client client, String address, int port)
	    throws ConnectException {
	this.client = client;
	try {
	    InetAddress addr = InetAddress.getByName(address);
	    Socket sock = new Socket();
	    sock.connect(new InetSocketAddress(addr, port));
	    client.getLog().info(
		    "Connected to server: (addr=" + addr.getHostAddress()
			    + " port=" + port + ")");
	    connect(0, sock, new NetClientHandler(this, client));
	} catch (ConnectException e) {
	    throw e;
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (SecurityException e) {
	    client.getLog().severe(e.toString());
	    throw new ConnectException(e.toString());
	}
    }

    @Override
    public void dispose() {
	if (sock != null && sock.isConnected() && dOut != null) {
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
	if (sock != null && sock.isConnected() && dOut != null) {
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
}
