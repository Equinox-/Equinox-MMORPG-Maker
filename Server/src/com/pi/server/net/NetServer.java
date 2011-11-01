package com.pi.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import com.pi.common.net.client.NetClient;
import com.pi.common.net.client.PacketOutputStream;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;
import com.pi.server.Server;
import com.pi.server.client.Client;
import com.pi.server.constants.ServerConstants;
import com.pi.server.net.client.NetServerClient;

public class NetServer extends Thread {
    private final Server server;
    private ServerSocket sock;
    private final ClientListener cl;

    public NetServer(Server server, int port, ClientListener cl)
	    throws IOException {
	super(server.getThreadGroup(), null, "ClientListener");
	this.server = server;
	this.cl = cl;
	sock = new ServerSocket(port);
	start();
    }

    public boolean isConnected() {
	return sock != null && sock.isBound() && !sock.isClosed();
    }

    @Override
    public void run() {
	server.getLog().finer("Starting client listener thread");
	while (isConnected()) {
	    try {
		addClient(sock.accept());
	    } catch (IOException ignored) {
	    }
	}
	server.getLog().finer("Killed client listener thread");
    }

    private void addClient(Socket src) {
	if (server.getClientManager().hasAvaliableSlot()) {
	    Client client = new Client(server, new NetServerClient(server, src));
	    if (server.getClientManager().registerClient(client) >= 0) {
		server.getLog().info("Client connected: " + client.toString());
		if (cl != null)
		    cl.clientConnected(client);
	    }
	    return;
	}
	try {
	    Packet0Disconnect p = new Packet0Disconnect("Max Clients",
		    "The server has reached the maximum client amount, "
			    + ServerConstants.MAX_CLIENTS);
	    PacketOutputStream d = new PacketOutputStream(src.getOutputStream());
	    p.writePacket(d);
	    d.flush();
	    d.close();
	    src.close();
	} catch (IOException e) {
	    e.printStackTrace(server.getLog().getErrorStream());
	}
    }

    public void dispose() {
	try {
	    sock.close();
	    for (Client c : server.getClientManager().registeredClients()
		    .values())
		if (c.getNetClient() != null)
		    c.getNetClient().dispose();
	} catch (IOException e) {
	    e.printStackTrace(server.getLog().getErrorStream());
	}
    }

    public void sendTo(Packet packet, int... ids) throws IOException {
	for (int id : ids) {
	    Client nClient = server.getClientManager().getClient(id);
	    if (nClient != null && nClient.getNetClient() != null)
		nClient.getNetClient().send(packet);
	}
    }

    public void sendToAll(Packet packet) throws IOException {
	for (Client nClient : server.getClientManager().registeredClients()
		.values()) {
	    if (nClient != null && nClient.getNetClient() != null)
		nClient.getNetClient().send(packet);
	}
    }
}
