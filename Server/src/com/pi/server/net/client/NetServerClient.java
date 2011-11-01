package com.pi.server.net.client;

import java.net.Socket;

import com.pi.common.database.Account;
import com.pi.common.debug.PILogger;
import com.pi.common.game.Entity;
import com.pi.common.net.client.NetClient;
import com.pi.server.Server;
import com.pi.server.client.Client;

public class NetServerClient extends NetClient {
    private final Server server;

    public NetServerClient(Server server, Socket sock) {
	this.server = server;
	connect(sock, new NetServerHandler(server, this));
    }

    @Override
    public PILogger getLog() {
	return server.getLog();
    }

    @Override
    public void dispose() {
	super.dispose();
	for (Client cli:server.getClientManager().registeredClients().values()){
	    if (cli.getNetClient()!=null && cli.getNetClient().equals(this)){
		server.getClientManager().disposeClient(cli);
	    }
	}
    }

    @Override
    public void dispose(String reason, String details) {
	/*
	 * if (sock != null && isConnected() && dOut != null &&
	 * !sock.isOutputShutdown()) { try { Packet p = new
	 * Packet0Disconnect(reason, details); p.writePacket(dOut);
	 * dOut.flush(); } catch (Exception e) {
	 * e.printStackTrace(server.getLog().getErrorStream()); } }
	 */
	dispose();
    }

    @Override
    public ThreadGroup getThreadGroup() {
	return server.getThreadGroup();
    }

}
