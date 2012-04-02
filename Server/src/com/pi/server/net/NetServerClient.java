package com.pi.server.net;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.pi.common.debug.PILogger;
import com.pi.common.net.NetChangeRequest;
import com.pi.common.net.NetClient;
import com.pi.common.net.NetHandler;
import com.pi.server.Server;
import com.pi.server.client.Client;

public class NetServerClient extends NetClient {
    private final Server server;
    private NetHandler handler;
    private int clientID = -1;
    private Client cliRef = null;

    public NetServerClient(final Server server, final SocketChannel socket) {
	super(socket);
	this.server = server;
	this.handler = new NetServerHandler(server, this);
    }

    public void bindClient(Client c) {
	if (cliRef == null)
	    cliRef = c;
	else
	    throw new RuntimeException("Client is already bound!");
    }

    @Override
    public PILogger getLog() {
	return server.getLog();
    }

    public void bindToID(int id) {
	this.clientID = id;
    }

    public int getID() {
	return clientID;
    }

    public void dispose(String reason, String details) {// TODO
	server.getNetwork().deregisterSocketChannel(socket);
    }

    public void dispose() {
	server.getNetwork().deregisterSocketChannel(socket);
    }

    @Override
    public void processData(byte[] data, int off, int len) {
	server.getNetwork().getWorker().processData(this, data, off, len);
    }

    @Override
    public void addWriteRequest() {
	server.getNetwork().addChangeRequest(
		new NetChangeRequest(socket, NetChangeRequest.CHANGEOPS,
			SelectionKey.OP_WRITE));
    }

    @Override
    public String getSuffix() {
	return " on " + clientID;
    }

    @Override
    public void wakeSelector() {
	server.getNetwork().wakeSelector();
    }

    @Override
    public NetHandler getHandler() {
	return handler;
    }

    @Override
    public String toString() {
	return "NetClient[" + clientID + "," + super.getHostAddress() + "]";
    }
}
