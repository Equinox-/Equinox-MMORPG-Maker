package com.pi.server.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.pi.common.net.client.NetProcessingThread;
import com.pi.common.net.client.PacketOutputStream;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;
import com.pi.server.Server;
import com.pi.server.client.Client;
import com.pi.server.constants.ServerConstants;
import com.pi.server.net.client.NetServerClient;

public class NetServer extends Thread {
    private final Server server;
    private final Selector selector;
    private final ClientListener cl;
    private final ServerSocketChannel socketChannel;
    private final ServerSocket socket;
    private final NetProcessingThread procThread;

    public NetServer(Server server, int port, ClientListener cl)
	    throws IOException {
	super(server.getThreadGroup(), null, "NetworkServer");
	this.server = server;
	this.cl = cl;
	this.procThread = new NetProcessingThread(server.getThreadGroup(),
		"Server Packet Processor");

	// Create Selector
	selector = Selector.open();

	// Create Server Socket Channel
	socketChannel = ServerSocketChannel.open();
	socketChannel.configureBlocking(false);
	socketChannel.register(selector, SelectionKey.OP_ACCEPT);

	// Get and bind server socket
	socket = socketChannel.socket();
	socket.bind(new InetSocketAddress(port));

	start();
	procThread.start();
    }

    public boolean isConnected() {
	return socket != null && selector.isOpen() && socketChannel.isOpen()
		&& socket.isBound();
    }

    @Override
    public void run() {
	server.getLog().finer("Starting client listener thread");
	while (isConnected()) {
	    try {
		if (selector.select() == 0)
		    continue;

		Iterator<SelectionKey> keys = selector.selectedKeys()
			.iterator();
		while (keys.hasNext()) {
		    SelectionKey key = keys.next();
		    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
			addClient(socket.accept());
		    } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
			SocketChannel sc = null;
			sc = (SocketChannel) key.channel();
			Object cl = key.attachment();
			if (cl instanceof Client) {
			    Client client = (Client) cl;
			    int ok = client.getNetClient().readPacket(sc);
			    // If the connection is dead, then remove it
			    // from the selector and close it
			    if (ok == -1) {
				server.getLog().severe("Packet ERROR: (Unable to read)");
				key.cancel();
				client.dispose();
			    }
			}
		    }
		}
	    } catch (IOException e) {
		e.printStackTrace(server.getLog().getErrorStream());
	    }
	}
	server.getLog().finer("Killed client listener thread");
    }

    private void addClient(Socket src) {
	int clientID = server.getClientManager().getAvaliableID();
	try {
	    if (clientID >= 0) {
		Client client = new Client(server, new NetServerClient(server,
			procThread, src), clientID);
		SocketChannel channel = src.getChannel();
		channel.register(selector, SelectionKey.OP_READ, client);
		if (server.getClientManager().registerClient(client) >= 0) {
		    server.getLog().info(
			    "Client connected: " + client.toString());
		    if (cl != null)
			cl.clientConnected(client);
		} else {
		    throw new Exception(
			    "The server was unable to register you as a client!");
		}
	    } else {
		throw new Exception(
			"The server has reached the maximum client amount, "
				+ ServerConstants.MAX_CLIENTS);
	    }
	} catch (Exception e) {
	    try {
		Packet0Disconnect p = new Packet0Disconnect("Error",
			e.getMessage());
		PacketOutputStream d = new PacketOutputStream(
			src.getOutputStream());
		p.writePacket(d);
		d.flush();
		d.close();
		src.close();
	    } catch (IOException fatal) {
		fatal.printStackTrace(server.getLog().getErrorStream());
	    }
	}
    }

    public void dispose() {
	try {
	    socket.close();
	    socketChannel.close();
	    selector.close();
	    Client c;
	    for (int i = 0; i < server.getClientManager().registeredClients()
		    .size(); i++) {
		c = server.getClientManager().getClient(i);
		if (c != null)
		    c.dispose();
	    }
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
	Client c;
	for (int i = 0; i < server.getClientManager().registeredClients()
		.size(); i++) {
	    c = server.getClientManager().getClient(i);
	    if (c != null && c.getNetClient() != null)
		c.getNetClient().send(packet);
	}
    }

    public NetProcessingThread getNetProcessor() {
	return procThread;
    }
}
