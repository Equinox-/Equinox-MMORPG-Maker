package com.pi.server.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pi.common.contants.Direction;
import com.pi.common.database.Location;
import com.pi.common.database.SectorLocation;
import com.pi.common.game.Entity;
import com.pi.common.game.ObjectHeap;
import com.pi.common.net.packet.Packet10EntityDataRequest;
import com.pi.common.net.packet.Packet16EntityMove;
import com.pi.common.net.packet.Packet7EntityTeleport;
import com.pi.common.net.packet.Packet8EntityDispose;
import com.pi.common.net.packet.Packet9EntityData;
import com.pi.server.Server;
import com.pi.server.client.Client;
import com.pi.server.constants.ServerConstants;

/**
 * A class to manage all the entities registered with this server.
 * 
 * @author Westin
 * 
 */
public class ServerEntityManager {
	/**
	 * The map of registered entities.
	 */
	private final ObjectHeap<ServerEntity> entityMap =
			new ObjectHeap<ServerEntity>();
	/**
	 * The server this entity manager is bound to.
	 */
	private final Server server;

	/**
	 * Create an entity manager bound to the provided client.
	 * 
	 * @param sServer the server to bind to
	 */
	public ServerEntityManager(final Server sServer) {
		this.server = sServer;
	}

	/**
	 * Gets an iterator instance that cycles through all the entities registered
	 * to this manager.
	 * 
	 * @return the iterator
	 */
	public final Iterator<ServerEntity> getEntities() {
		return entityMap.iterator();
	}

	/**
	 * Requests the entity data for the given client and packet.
	 * 
	 * @param id the client id
	 * @param p the request packet
	 */
	public final void requestData(final int id,
			final Packet10EntityDataRequest p) {
		Entity ent = getEntity(p.entityID).getWrappedEntity();
		if (ent != null) {
			Client cli = server.getClientManager().getClient(id);
			if (cli != null && cli.getNetClient() != null) {
				cli.getNetClient().send(
						Packet9EntityData.create(ent));
			}
		}
	}

	/**
	 * Registers the given entity with this manager.
	 * 
	 * @param e the entity to register
	 * @return <code>true</code> if registered, <code>false</code> if not
	 */
	public final boolean registerEntity(final Entity e) {
		int id = 0;
		while (true) {
			if (entityMap.get(id) == null) {
				break;
			}
			id++;
		}
		if (e.setEntityID(id)) {
			entityMap.set(id, new ServerEntity(e));
			return true;
		}
		return false;
	}

	/**
	 * Removes the entity registered to the given identification number from the
	 * mapping.
	 * 
	 * @param id the entity id to remove
	 * @return the entity that was removed, or <code>null</code> if there wasn't
	 *         one removed
	 */
	public final ServerEntity deRegisterEntity(final int id) {
		return entityMap.remove(id);
	}

	/**
	 * Gets all the entities if the given sector.
	 * 
	 * @param loc the sector location
	 * @return the entities if the given sector
	 */
	public final List<ServerEntity> getEntitiesInSector(
			final SectorLocation loc) {
		List<ServerEntity> sector =
				new ArrayList<ServerEntity>();
		for (ServerEntity e : entityMap) {
			if (loc.containsLocation(e.getWrappedEntity())) {
				sector.add(e);
			}
		}
		return sector;
	}

	/**
	 * Gets all the entities within the specified distance of the given
	 * location.
	 * 
	 * @param l the base location
	 * @param maxDist the maximum distance
	 * @return the entities within the given distance
	 */
	public final List<ServerEntity> getEntitiesWithin(
			final Location l, final int maxDist) {
		List<ServerEntity> entities =
				new ArrayList<ServerEntity>();
		for (ServerEntity e : entityMap) {
			if (Location.dist(l, e.getWrappedEntity()) <= maxDist) {
				entities.add(e);
			}
		}
		return entities;
	}

	/**
	 * Gets the entity registered to the given id, or <code>null</code> if there
	 * isn't one registered.
	 * 
	 * @param id the identification number
	 * @return the entity
	 */
	public final ServerEntity getEntity(final int id) {
		return entityMap.get(id);
	}

	/**
	 * Sends an entity teleport packet to all nearby clients for the given
	 * entity, between the given location.
	 * 
	 * @param entity the entity to send the packet for
	 * @param from the entity's old location
	 * @param to the entity's new location
	 */
	public final void sendEntityTeleport(final int entity,
			final Location from, final Location to) {
		ServerEntity e = getEntity(entity);
		if (e != null) {
			for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
				Client cli =
						server.getClientManager().getClient(i);
				if (cli != null && cli.getEntity() != null
						&& cli.getNetClient() != null) {
					int nDist =
							Location.dist(cli.getEntity(), to);
					int oDist =
							Location.dist(cli.getEntity(), from);
					if (nDist <= ServerConstants.ENTITY_UPDATE_DIST) {
						cli.getNetClient().send(
								Packet7EntityTeleport.create(e
										.getWrappedEntity()));
					} else if (oDist > ServerConstants.ENTITY_DISPOSE_DIST) {
						cli.getNetClient().send(
								Packet8EntityDispose
										.create(entity));
					}
				}
			}
		}
	}

	/**
	 * Sends an entity dispose packet to all the clients within disposal
	 * distance of the given entity.
	 * 
	 * @param entity the entity to dispose
	 */
	public final void sendEntityDispose(final int entity) {
		Entity e = deRegisterEntity(entity).getWrappedEntity();
		if (e != null) {
			for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
				Client cli =
						server.getClientManager().getClient(i);
				if (cli != null && cli.getEntity() != null
						&& cli.getNetClient() != null) {
					int dist = Location.dist(cli.getEntity(), e);
					if (dist <= ServerConstants.ENTITY_DISPOSE_DIST) {
						cli.getNetClient().send(
								Packet8EntityDispose
										.create(entity));
					}
				}
			}
		}
	}

	/**
	 * Sends all nearby entity information to the given client.
	 * 
	 * @param cli the client to send the information to
	 */
	public final void sendClientEntities(final Client cli) {
		if (cli != null && cli.getEntity() != null
				&& cli.getNetClient() != null) {
			for (ServerEntity e : entityMap) {
				if (e != null) {
					int dist =
							Location.dist(e.getWrappedEntity(),
									cli.getEntity());
					if (dist <= ServerConstants.ENTITY_UPDATE_DIST) {
						cli.getNetClient().send(
								Packet9EntityData.create(e
										.getWrappedEntity()));
					}
				}
			}
		}
	}

	/**
	 * Sends an entity movement packet for the given entity to all nearby
	 * clients.
	 * 
	 * @param entity the entity to inform on
	 * @param from the starting location
	 * @param to the ending location
	 * @param dir the direction the movement was in
	 */
	public final void sendEntityMove(final int entity,
			final Location from, final Location to,
			final Direction dir) {
		ServerEntity e = getEntity(entity);
		if (e != null) {
			for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
				Client cli =
						server.getClientManager().getClient(i);
				if (cli != null
						&& cli.getEntity() != null
						&& cli.getNetClient() != null
						&& cli.getEntity().getEntityID() != entity) {
					int nDist =
							Location.dist(cli.getEntity(), to);
					int oDist =
							Location.dist(cli.getEntity(), from);
					if (nDist <= ServerConstants.ENTITY_UPDATE_DIST) {
						cli.getNetClient().send(
								Packet16EntityMove.create(
										entity, to));
					} else if (oDist > ServerConstants.ENTITY_DISPOSE_DIST) {
						cli.getNetClient().send(
								Packet8EntityDispose
										.create(entity));
					}
				}
			}
		}
	}

	/**
	 * Gets the number of registered entities for this entity manager.
	 * 
	 * @return the number of registered entities
	 */
	public final int entityCount() {
		return entityMap.numElements();
	}
}
