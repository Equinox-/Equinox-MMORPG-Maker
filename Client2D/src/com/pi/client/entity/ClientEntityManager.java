package com.pi.client.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pi.client.Client;
import com.pi.common.database.Location;
import com.pi.common.database.SectorLocation;
import com.pi.common.game.Entity;
import com.pi.common.game.EntityType;
import com.pi.common.game.ObjectHeap;

/**
 * A class to manage all the entities registered with this client.
 * 
 * @author Westin
 * 
 */
public class ClientEntityManager {
	/**
	 * The map of registered entities.
	 */
	private final ObjectHeap<ClientEntity> entityMap =
			new ObjectHeap<ClientEntity>();
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
	 * @param sClient the client to bind to
	 */
	public ClientEntityManager(final Client sClient) {
		this.client = sClient;
	}

	/**
	 * Checks if there is an entity by the provided ID registered with this
	 * entity manager.
	 * 
	 * @param id the entity id
	 * @return if the entity is registered
	 */
	public final boolean isEntityRegistered(final int id) {
		return entityMap.get(id) != null;
	}

	/**
	 * Sets the local entity ID to the given value.
	 * 
	 * @param id the local entity id
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
		return getEntity(localEntityID);
	}

	/**
	 * Creates and registers an entity with the given entity type to this entity
	 * manager.
	 * 
	 * @param eType the entity type
	 * @return the registered entity or <code>null</code> if it wasn't
	 *         registered
	 */
	public final Entity registerEntity(final EntityType eType) {
		int id = 0;
		while (true) {
			if (entityMap.get(id) == null) {
				break;
			}
			id++;
		}
		Entity e = eType.createInstance();
		if (e != null) {
			if (e.setEntityID(id)) {
				entityMap.set(id, new ClientEntity(e));
				return e;
			}
		}
		return null;
	}

	/**
	 * Creates and registers an entity with the given entity type to this entity
	 * manager with the given identification number.
	 * 
	 * @param eType the entity type
	 * @param id the identification number to register to
	 * @return the registered entity or <code>null</code> if it wasn't
	 *         registered
	 */
	public final Entity registerEntity(final EntityType eType,
			final int id) {
		if (getEntity(id) != null) {
			return null;
		}
		Entity e = eType.createInstance();
		if (e != null) {
			if (e.setEntityID(id)) {
				entityMap.set(id, new ClientEntity(e));
				return e;
			}
		}
		return null;
	}

	/**
	 * Removes an entity from this entity manager.
	 * 
	 * @param id the entity id to purge.
	 * @return <code>true</code> if the entity was removed from the entity map,
	 *         <code>false</code> if it wasn't registered
	 */
	public final boolean deRegisterEntity(final int id) {
		return entityMap.remove(id) != null;
	}

	/**
	 * Get all the entities in the provided sector.
	 * 
	 * @param loc the sector to scan
	 * @return the entities in the sector
	 */
	public final List<ClientEntity> getEntitiesInSector(
			final SectorLocation loc) {
		List<ClientEntity> sector =
				new ArrayList<ClientEntity>();
		for (ClientEntity e : entityMap) {
			if (loc.containsLocation(e.getWrappedEntity())) {
				sector.add(e);
			}
		}
		return sector;
	}

	/**
	 * Get all the entities within the provided distance of the specified
	 * location.
	 * 
	 * @param l the base location
	 * @param maxDist the maximum distance from the base
	 * @return the entities within the specified range
	 */
	public final List<ClientEntity> getEntitiesWithin(
			final Location l, final int maxDist) {
		List<ClientEntity> entities =
				new ArrayList<ClientEntity>();
		for (ClientEntity e : entityMap) {
			if (Location.dist(l, e.getWrappedEntity()) <= maxDist) {
				entities.add(e);
			}
		}
		return entities;
	}

	/**
	 * Gets the entity registered at the provided id.
	 * 
	 * @param id the entity id to fetch
	 * @return the entity, or <code>null</code> if no entity is registered
	 */
	public final ClientEntity getEntity(final int id) {
		return (ClientEntity) entityMap.get(id);
	}

	/**
	 * Gets the number of registered entities for this entity manager.
	 * 
	 * @return the number of registered entities
	 */
	public final int entityCount() {
		return entityMap.numElements();
	}

	/**
	 * Gets an iterator instance that cycles through all the entities registered
	 * to this manager.
	 * 
	 * @return the iterator
	 */
	public final Iterator<ClientEntity> getEntities() {
		return entityMap.iterator();
	}

	/**
	 * Gets the client instance bound to this entity manager.
	 * 
	 * @return the client instance
	 */
	public final Client getClient() {
		return client;
	}
}
