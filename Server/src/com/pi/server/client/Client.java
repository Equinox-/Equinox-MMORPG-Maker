package com.pi.server.client;

import com.pi.common.database.Account;
import com.pi.common.game.Entity;
import com.pi.common.net.client.NetClient;
import com.pi.common.net.packet.Packet10LocalEntityID;
import com.pi.server.Server;

public class Client {
    private Account acc;
    private Entity entity;
    private final NetClient network;
    private final Server server;
    private final int clientID;

    public Client(Server server, NetClient client) {
	this.server = server;
	this.network = client;
	server.getClientManager().registerClient(this);
	clientID = server.getClientManager().getClientID(this);
	client.bindToID(clientID);
    }

    public void bindAccount(Account account) {
	if (this.entity != null) {
	    server.getServerEntityManager().deRegisterEntity(
		    this.entity.getEntityID());
	}
	this.acc = account;
	this.entity = new Entity(account.getEntityDef());
	server.getServerEntityManager().registerEntity(entity);
	network.send(Packet10LocalEntityID.getPacket(entity.getEntityID()));
    }

    public void dispose() {
	String desc = this.toString();
	if (entity != null && entity.getEntityID() != -1)
	    server.getServerEntityManager().deRegisterEntity(
		    entity.getEntityID());
	if (network != null && !network.isQuitting())
	    network.dispose();
	server.getLog().info("Client disconnected: " + desc);
    }

    public boolean isRegistered() {
	return clientID != -1;
    }

    public Entity getEntity() {
	return entity;
    }

    public Account getAccount() {
	return acc;
    }

    public NetClient getNetClient() {
	return network;
    }

    @Override
    public String toString() {
	return "Client(id=" + clientID
		+ (network != null ? " ,network=" + network.toString() : "")
		+ (acc != null ? " ,account=" + acc.toString() : "")
		+ (entity != null ? " ,entity=" + entity.toString() : "");
    }
}
