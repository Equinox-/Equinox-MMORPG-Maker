package com.pi.server.client;

import com.pi.common.database.Account;
import com.pi.common.game.Entity;
import com.pi.common.net.client.NetClient;
import com.pi.server.Server;

public class Client {
    private final Account acc;
    private final Entity entity;
    private final NetClient network;
    private final Server server;
    private final int clientID;

    public Client(Server server, NetClient client, Account account) {
	this.server = server;
	this.network = client;
	this.acc = account;
	this.entity = new Entity(account.getEntityDef());
	server.getServerEntityManager().registerEntity(entity);
	server.getClientManager().registerClient(this);
	clientID = server.getClientManager().getClientID(this);
    }

    public void dispose() {
	if (entity != null && entity.getEntityID() != -1)
	    server.getServerEntityManager().deRegisterEntity(
		    entity.getEntityID());
	if (network != null && !network.isQuitting())
	    network.dispose();
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
}
