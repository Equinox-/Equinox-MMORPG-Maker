package com.pi.client.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pi.client.Client;
import com.pi.common.database.Location;
import com.pi.common.database.SectorLocation;
import com.pi.common.game.Entity;

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
	private final Map<Integer, ClientEntity> entityMap =
			Collections
					.synchronizedMap(new HashMap<Integer, ClientEntity>());
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
		return entityMap.containsKey(id);
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
	 * Registers the provided entity with this entity manager.
	 * 
	 * @param eI the entity to register
	 * @return <code>true</code> if the entity was registered,
	 *         <code>false</code> if not
	 */
	public final boolean saveEntity(final Entity eI) {
		ClientEntity e = new ClientEntity(eI);
		int id = eI.getEntityID();
		if (id == -1) {
			while (true) {
				if (!entityMap.containsKey(id)) {
					break;
				}
				id++;
			}
			if (e.getWrappedEntity().setEntityID(id)) {
				entityMap.put(id, e);
				return true;
			}
		} else {
			entityMap.put(e.getWrappedEntity().getEntityID(), e);
			return true;
		}
		return false;
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
		for (ClientEntity e : entityMap.values()) {
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
		for (ClientEntity e : entityMap.values()) {
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
	 * Gets all the registered entities in an unmodifiable map.
	 * 
	 * @return an unmodifiable version of the entity registration
	 */
	public final Map<Integer, ClientEntity> registeredEntities() {
		return Collections.unmodifiableMap(entityMap);
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
