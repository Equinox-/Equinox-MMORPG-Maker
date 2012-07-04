package com.pi.server.client;

import com.pi.common.database.Account;
import com.pi.common.game.Entity;
import com.pi.common.net.packet.Packet11LocalEntityID;
import com.pi.server.Server;
import com.pi.server.constants.Configuration;
import com.pi.server.net.NetServerClient;

/**
 * Class representing a client connected to the server, wrapping the account,
 * entity, and network model.
 * 
 * @author Westin
 * 
 */
public class Client {
	/**
	 * The account this client connected on.
	 */
	private Account acc;
	/**
	 * The visual entity that this client controls.
	 */
	private Entity entity;
	/**
	 * The network model this client sends packets on.
	 */
	private final NetServerClient network;
	/**
	 * The server instance this client is bound to.
	 */
	private final Server server;
	/**
	 * The client identification number that this client has.
	 */
	private final int clientID;

	/**
	 * Creates a client instance with the given server and network model.
	 * 
	 * @param sServer the server instance
	 * @param sClient the network model
	 */
	public Client(final Server sServer,
			final NetServerClient sClient) {
		this.server = sServer;
		this.network = sClient;
		server.getClientManager().registerClient(this);
		this.clientID =
				server.getClientManager().getClientID(this);
		network.bindToID(clientID);
	}

	/**
	 * Binds this client to the given account, and creates the entity from the
	 * account data.
	 * 
	 * @param account the account to bind to
	 */
	public final void bindAccount(final Account account) {
		if (this.entity != null) {
			server.getEntityManager().deRegisterEntity(
					this.entity.getEntityID());
		}
		this.acc = account;
		this.entity =
				server.getEntityManager().spawnEntity(
						server.getDefs().getEntityLoader()
								.getDef(account.getEntityDef()),
						account.getLocation());
		network.send(Packet11LocalEntityID.create(entity
				.getEntityID()));
		// TODO Find a better way to request entities for clients on move
		server.getEntityManager().sendClientEntities(this);
	}

	/**
	 * Disposes and removes this client from the entity and network registry.
	 */
	public final void dispose() {
		String desc = this.toString();
		if (entity != null) {
			acc.setLocation(entity.x, entity.plane, entity.z);
			server.getEntityManager().sendEntityDispose(
					entity.getEntityID());
		}
		if (network != null) {
			network.dispose();
		}
		server.getClientManager().removeFromRegistry(getID());
		server.getLog().info("Client disconnected: " + desc);
	}

	/**
	 * Checks if this entity is registered to the client manager.
	 * 
	 * @return <code>true</code> if it's registered, <code>false</code> if not
	 */
	public final boolean isRegistered() {
		return clientID != -1;
	}

	/**
	 * Gets the visual entity that this client controls.
	 * 
	 * @return the entity
	 */
	public final Entity getEntity() {
		return entity;
	}

	/**
	 * Gets the account this client is bound to.
	 * 
	 * @return the account
	 */
	public final Account getAccount() {
		return acc;
	}

	/**
	 * Gets the network model bound to this client.
	 * 
	 * @return the network model
	 */
	public final NetServerClient getNetClient() {
		return network;
	}

	@Override
	public final String toString() {
		StringBuilder desc = new StringBuilder();
		desc.append("Client(id=" + clientID + " ,network=");
		if (network != null) {
			desc.append(network.toString());
		} else {
			desc.append("none");
		}
		desc.append(" ,account=");
		if (acc != null) {
			desc.append(acc.toString());
		} else {
			desc.append("none");
		}
		desc.append(" ,entity=");
		if (entity != null) {
			desc.append(entity.toString());
		} else {
			desc.append("none");
		}
		return desc.toString();
	}

	/**
	 * Gets the identification number this client has with the client manager,
	 * or <code>-1</code> if unregistered.
	 * 
	 * @return the id number
	 */
	public final int getID() {
		return clientID;
	}

	/**
	 * Called when this client's entity dies.
	 */
	public final void onEntityDeath() {
		acc.setLocation(Configuration.SPAWN_POINT);
		this.entity =
				server.getEntityManager().spawnEntity(
						server.getDefs().getEntityLoader()
								.getDef(acc.getEntityDef()),
						acc.getLocation());
		network.send(Packet11LocalEntityID.create(entity
				.getEntityID()));
	}
}
