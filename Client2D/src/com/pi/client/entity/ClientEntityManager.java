package com.pi.client.entity;

import com.pi.client.Client;
import com.pi.common.game.entity.Entity;
import com.pi.common.game.entity.EntityManager;

/**
 * A class to manage all the entities registered with this client.
 * 
 * @author Westin
 * 
 */
public class ClientEntityManager extends EntityManager<ClientEntity> {
	/**
	 * The client this entity manager is bound to.
	 */
	private final Client client;
	/**
	 * The local entity id, or <code>-1</code> if unknown.
	 */
	private int localEntityID = -1;

	/**
	 * Create an entity manager bound to the provided client.
	 * 
	 * @param sClient
	 *            the client to bind to
	 */
	public ClientEntityManager(final Client sClient) {
		this.client = sClient;
	}

	/**
	 * Sets the local entity ID to the given value.
	 * 
	 * @param id
	 *            the local entity id
	 */
	public final void setLocalEntityID(final int id) {
		this.localEntityID = id;
	}

	/**
	 * Gets the local entity, as specified by
	 * {@link ClientEntityManager#setLocalEntityID(int)}.
	 * 
	 * @return the local entity.
	 */
	public final ClientEntity getLocalEntity() {
		return getEntityContainer(localEntityID);
	}

	/**
	 * Gets the client instance bound to this entity manager.
	 * 
	 * @return the client instance
	 */
	public final Client getClient() {
		return client;
	}

	@Override
	protected ClientEntity createEntityContainer(Entity entity) {
		return new ClientEntity(entity);
	}
}
