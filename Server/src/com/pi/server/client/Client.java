package com.pi.server.client;

import com.pi.common.database.Account;
import com.pi.common.game.Entity;
import com.pi.common.net.packet.Packet11LocalEntityID;
import com.pi.server.Server;
import com.pi.server.net.NetServerClient;

public class Client {
    private Account acc;
    private Entity entity;
    private final NetServerClient network;
    private final Server server;
    private final int clientID;

    public Client(Server server, NetServerClient client) {
	this.server = server;
	this.network = client;
	server.getClientManager().registerClient(this);
	this.clientID = server.getClientManager().getClientID(this);
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
	network.send(Packet11LocalEntityID.getPacket(entity.getEntityID()));
	// TODO Find a better way to request entities for clients on move
	server.getServerEntityManager().clientMove(this, null, entity);
    }

    public void dispose() {
	String desc = this.toString();
	if (entity != null && entity.getEntityID() != -1)
	    server.getServerEntityManager().deRegisterEntity(
		    entity.getEntityID());
	if (network != null)
	    network.dispose();
	server.getClientManager().removeFromRegistry(getID());
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

    public NetServerClient getNetClient() {
	return network;
    }

    @Override
    public String toString() {
	return "Client(id=" + clientID
		+ (network != null ? " ,network=" + network.toString() : "")
		+ (acc != null ? " ,account=" + acc.toString() : "")
		+ (entity != null ? " ,entity=" + entity.toString() : "");
    }

    public int getID() {
	return clientID;
    }
}
