package com.pi.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.pi.common.net.client.NetClient;
import com.pi.common.net.client.PacketOutputStream;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;
import com.pi.server.Server;
import com.pi.server.net.client.NetServerClient;

public class NetServer extends Thread {
    public static int MAX_CLIENTS = 10;
    private final Server server;
    private ServerSocket sock;
    private Map<Integer, NetClient> clientMap;
    private final ClientListener cl;

    public NetServer(Server server, int port, ClientListener cl)
	    throws IOException {
	super(server.getThreadGroup(), null, "ClientListener");
	this.server = server;
	this.cl = cl;
	this.clientMap = new HashMap<Integer, NetClient>();
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
	int id = getAvaliableID();
	if (id != -1) {
	    clientMap.put(id, new NetServerClient(server, id, src));
	    server.getLog().info(
		    "Client connected: " + clientMap.get(id).toString());
	    if (cl != null)
		cl.clientConnected(clientMap.get(id));
	} else {
	    try {
		Packet0Disconnect p = new Packet0Disconnect("Max Clients",
			"The server has reached the maximum client amount, "
				+ MAX_CLIENTS);
		PacketOutputStream d = new PacketOutputStream(
			src.getOutputStream());
		p.writePacket(d);
		d.flush();
		d.close();
		src.close();
	    } catch (IOException e) {
		e.printStackTrace(server.getLog().getErrorStream());
	    }
	}
    }

    public void removeClient(int id, String reason, String details) {
	NetClient c = clientMap.get(id);
	if (cl != null)
	    cl.clientDisconnected(c);
	c.dispose(reason, details);
    }

    public Map<Integer, NetClient> getClientMap() {
	return clientMap;
    }

    public NetClient getClient(int id) {
	return clientMap.get(id);
    }

    private int getAvaliableID() {
	for (int id = 0; id < MAX_CLIENTS; id++) {
	    if (!clientMap.containsKey(id))
		return id;
	}
	return -1;
    }

    public void dispose() {
	try {
	    sock.close();
	} catch (IOException e) {
	    e.printStackTrace(server.getLog().getErrorStream());
	}
	for (int id : clientMap.keySet()) {
	    removeClient(id, "Socket Closed", "Server shutdown");
	}
    }

    public void sendTo(Packet packet, int... ids) throws IOException {
	for (int id : ids) {
	    NetClient nClient = clientMap.get(id);
	    if (nClient != null)
		nClient.send(packet);
	}
    }

    public void sendToAll(Packet packet) throws IOException {
	for (NetClient nClient : clientMap.values()) {
	    if (nClient != null)
		nClient.send(packet);
	}
    }
}
