package com.pi.server.entity;

import com.pi.common.constants.Direction;
import com.pi.common.database.Location;
import com.pi.common.game.entity.Entity;
import com.pi.common.game.entity.EntityManager;
import com.pi.common.net.packet.Packet10EntityDataRequest;
import com.pi.common.net.packet.Packet16EntityMove;
import com.pi.common.net.packet.Packet21EntityFace;
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
public class ServerEntityManager extends EntityManager<ServerEntity> {
	/**
	 * The server this entity manager is bound to.
	 */
	private final Server server;

	/**
	 * Create an entity manager bound to the provided client.
	 * 
	 * @param sServer
	 *            the server to bind to
	 */
	public ServerEntityManager(final Server sServer) {
		this.server = sServer;
	}

	/**
	 * Requests the entity data for the given client and packet.
	 * 
	 * @param id
	 *            the client id
	 * @param p
	 *            the request packet
	 */
	public final void requestData(final int id,
			final Packet10EntityDataRequest p) {
		Entity ent = getEntity(p.entityID);
		if (ent != null) {
			Client cli = server.getClientManager().getClient(id);
			if (cli != null && cli.getNetClient() != null) {
				cli.getNetClient().send(Packet9EntityData.create(ent));
			}
		}
	}

	/**
	 * Sends an entity teleport packet to all nearby clients for the given
	 * entity, between the given location.
	 * 
	 * @param entity
	 *            the entity to send the packet for
	 * @param from
	 *            the entity's old location
	 * @param to
	 *            the entity's new location
	 */
	public final void sendEntityTeleport(final int entity, final Location from,
			final Location to) {
		ServerEntity e = getEntityContainer(entity);
		if (e != null) {
			for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
				Client cli = server.getClientManager().getClient(i);
				if (cli != null && cli.getEntity() != null
						&& cli.getNetClient() != null) {
					int nDist = Location.dist(cli.getEntity(), to);
					int oDist = Location.dist(cli.getEntity(), from);
					if (nDist <= ServerConstants.ENTITY_UPDATE_DIST) {
						cli.getNetClient().send(
								Packet7EntityTeleport.create(e
										.getWrappedEntity()));
					} else if (oDist > ServerConstants.ENTITY_DISPOSE_DIST) {
						cli.getNetClient().send(
								Packet8EntityDispose.create(entity));
					}
				}
			}
		}
	}

	/**
	 * Sends an entity dispose packet to all the clients within disposal
	 * distance of the given entity.
	 * 
	 * @param entity
	 *            the entity to dispose
	 */
	public final void sendEntityDispose(final int entity) {
		Entity e = deRegisterEntity(entity).getWrappedEntity();
		if (e != null) {
			for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
				Client cli = server.getClientManager().getClient(i);
				if (cli != null && cli.getEntity() != null
						&& cli.getNetClient() != null) {
					int dist = Location.dist(cli.getEntity(), e);
					if (dist <= ServerConstants.ENTITY_DISPOSE_DIST) {
						cli.getNetClient().send(
								Packet8EntityDispose.create(entity));
					}
				}
			}
		}
	}

	/**
	 * Sends entity data for the given entity to all nearby clients.
	 * 
	 * @param entity
	 *            the entity ID
	 */
	public final void sendSpawnEntity(final int entity) {
		ServerEntity e = getEntityContainer(entity);
		if (e != null) {
			for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
				Client cli = server.getClientManager().getClient(i);
				if (cli != null && cli.getEntity() != null
						&& cli.getNetClient() != null) {
					int dist = Location.dist(cli.getEntity(), cli.getEntity());
					if (dist <= ServerConstants.ENTITY_UPDATE_DIST) {
						cli.getNetClient().send(
								Packet9EntityData.create(e.getWrappedEntity()));
					}
				}
			}
		}
	}

	/**
	 * Sends all nearby entity information to the given client.
	 * 
	 * @param cli
	 *            the client to send the information to
	 */
	public final void sendClientEntities(final Client cli) {
		if (cli != null && cli.getEntity() != null
				&& cli.getNetClient() != null) {
			for (ServerEntity e : getEntityMap()) {
				if (e != null) {
					int dist = Location.dist(e.getWrappedEntity(),
							cli.getEntity());
					if (dist <= ServerConstants.ENTITY_UPDATE_DIST) {
						cli.getNetClient().send(
								Packet9EntityData.create(e.getWrappedEntity()));
					}
				}
			}
		}
	}

	/**
	 * Sends an entity movement packet for the given entity to all nearby
	 * clients.
	 * 
	 * @param entity
	 *            the entity to inform on
	 * @param from
	 *            the starting location
	 * @param to
	 *            the ending location
	 * @param dir
	 *            the direction the movement was in
	 */
	public final void sendEntityMove(final int entity, final Location from,
			final Location to, final Direction dir) {
		ServerEntity e = getEntityContainer(entity);
		if (e != null) {
			for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
				Client cli = server.getClientManager().getClient(i);
				if (cli != null && cli.getEntity() != null
						&& cli.getNetClient() != null
						&& cli.getEntity().getEntityID() != entity) {
					int nDist = Location.dist(cli.getEntity(), to);
					int oDist = Location.dist(cli.getEntity(), from);
					if (nDist <= ServerConstants.ENTITY_UPDATE_DIST) {
						cli.getNetClient().send(
								Packet16EntityMove.create(entity, to));
					} else if (oDist > ServerConstants.ENTITY_DISPOSE_DIST) {
						cli.getNetClient().send(
								Packet8EntityDispose.create(entity));
					}
				}
			}
		}
	}

	/**
	 * Sends an entity rotate packet for the given entity to all nearby clients.
	 * 
	 * @param entity
	 *            the entity to inform on
	 * @param face
	 *            the entity's new rotation
	 */
	public final void sendEntityRotate(final int entity, final Direction face) {
		ServerEntity e = getEntityContainer(entity);
		if (e != null) {
			for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
				Client cli = server.getClientManager().getClient(i);
				if (cli != null && cli.getEntity() != null
						&& cli.getNetClient() != null
						&& cli.getEntity().getEntityID() != entity) {
					int dist = Location.dist(cli.getEntity(),
							e.getWrappedEntity());
					if (dist <= ServerConstants.ENTITY_UPDATE_DIST) {
						cli.getNetClient().send(
								Packet21EntityFace.create(entity, face));
					}
				}
			}
		}
	}

	@Override
	protected ServerEntity createEntityContainer(Entity entity) {
		return new ServerEntity(server.getDefs().getEntityLoader(), entity);
	}
}
