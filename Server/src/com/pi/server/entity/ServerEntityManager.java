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

public class ServerEntityManager {
	private final ObjectHeap<ServerEntity> entityMap =
			new ObjectHeap<ServerEntity>();
	private final Server server;

	public ServerEntityManager(Server server) {
		this.server = server;
	}

	public Iterator<ServerEntity> getEntities() {
		return entityMap.iterator();
	}

	public void requestData(int id, Packet10EntityDataRequest p) {
		Entity ent = getEntity(p.entityID).getWrappedEntity();
		if (ent != null) {
			Client cli = server.getClientManager().getClient(id);
			if (cli != null && cli.getNetClient() != null) {
				cli.getNetClient().send(
						Packet9EntityData.create(ent));
			}
		}
	}

	public boolean registerEntity(Entity e) {
		int id = 0;
		while (true) {
			if (entityMap.get(id) == null)
				break;
			id++;
		}
		if (e.setEntityID(id)) {
			entityMap.set(id, new ServerEntity(e));
			return true;
		}
		return false;
	}

	public ServerEntity deRegisterEntity(int id) {
		return entityMap.remove(id);
	}

	public List<ServerEntity> getEntitiesInSector(
			SectorLocation loc) {
		List<ServerEntity> sector =
				new ArrayList<ServerEntity>();
		for (ServerEntity e : entityMap) {
			if (loc.containsLocation(e.getWrappedEntity())) {
				sector.add(e);
			}
		}
		return sector;
	}

	public List<ServerEntity> getEntitiesWithin(Location l,
			int maxDist) {
		List<ServerEntity> entities =
				new ArrayList<ServerEntity>();
		for (ServerEntity e : entityMap) {
			if (Location.dist(l, e.getWrappedEntity()) <= maxDist) {
				entities.add(e);
			}
		}
		return entities;
	}

	public ServerEntity getEntity(int id) {
		return entityMap.get(id);
	}

	public void sendEntityTeleport(int entity, Location from,
			Location to) {
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

	public void sendEntityDispose(int entity) {
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

	public void sendClientEntities(Client cli) {
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

	public void sendEntityMove(int entity, Location from,
			Location to, Direction dir) {
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

	public int entityCount() {
		return entityMap.numElements();
	}
}
