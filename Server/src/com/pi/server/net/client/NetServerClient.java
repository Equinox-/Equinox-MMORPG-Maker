package com.pi.server.net.client;

import java.net.Socket;

import com.pi.common.database.Account;
import com.pi.common.debug.PILogger;
import com.pi.common.game.Entity;
import com.pi.common.net.client.NetClient;
import com.pi.server.Server;

public class NetServerClient extends NetClient {
    private final Server server;
    private Account boundAccount;

    public NetServerClient(Server server, int id, Socket sock) {
	this.server = server;
	connect(id, sock, new NetServerHandler(server, this));
    }

    public void bindAccount(Account acc) {
	this.boundAccount = acc;
    }

    public Account getBoundAccount() {
	return boundAccount;
    }

    @Override
    public PILogger getLog() {
	return server.getLog();
    }

    @Override
    public void dispose() {
	super.dispose();
	server.getNetwork().getClientMap().remove(id);
	server.getLog().info("Client disconnected: " + this.toString());
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
