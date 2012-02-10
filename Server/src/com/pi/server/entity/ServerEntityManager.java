package com.pi.server.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pi.common.database.Location;
import com.pi.common.database.SectorLocation;
import com.pi.common.game.Entity;
import com.pi.common.game.EntityListener;
import com.pi.common.net.packet.Packet10EntityDataRequest;
import com.pi.common.net.packet.Packet7EntityMove;
import com.pi.common.net.packet.Packet8EntityDispose;
import com.pi.common.net.packet.Packet9EntityData;
import com.pi.server.Server;
import com.pi.server.client.Client;
import com.pi.server.constants.ServerConstants;

public class ServerEntityManager implements EntityListener {
    private final Map<Integer, Entity> entityMap = Collections
	    .synchronizedMap(new HashMap<Integer, Entity>());
    private final Server server;

    public ServerEntityManager(Server server) {
	this.server = server;
    }

    public void requestData(int id, Packet10EntityDataRequest p) {
	Entity ent = getEntity(p.entityID);
	if (ent != null) {
	    Client cli = server.getClientManager().getClient(id);
	    if (cli != null && cli.getNetClient() != null) {
		cli.getNetClient().send(Packet9EntityData.create(ent));
	    }
	}
    }

    public boolean registerEntity(Entity e) {
	e.setListener(this);
	int id = 0;
	while (true) {
	    if (!entityMap.containsKey(id))
		break;
	    id++;
	}
	if (e.setEntityID(id)) {
	    entityMap.put(id, e);
	    return true;
	}
	return false;
    }

    public boolean deRegisterEntity(int id) {
	return entityMap.remove(id) != null;
    }

    public List<Entity> getEntitiesInSector(SectorLocation loc) {
	List<Entity> sector = new ArrayList<Entity>();
	for (Entity e : entityMap.values()) {
	    if (loc.containsLocation(e)) {
		sector.add(e);
	    }
	}
	return sector;
    }

    public List<Entity> getEntitiesWithin(Location l, int maxDist) {
	List<Entity> entities = new ArrayList<Entity>();
	for (Entity e : entityMap.values()) {
	    if (Location.dist(l, e) <= maxDist) {
		entities.add(e);
	    }
	}
	return entities;
    }

    public Entity getEntity(int id) {
	return entityMap.get(id);
    }

    @Override
    public void entityMove(int entity, Location from, Location to) {
	Entity e = getEntity(entity);
	if (e != null) {
	    for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
		Client cli = server.getClientManager().getClient(i);
		if (cli != null && cli.getEntity() != null
			&& cli.getNetClient() != null) {
		    int nDist = Location.dist(cli.getEntity(), to);
		    int oDist = Location.dist(cli.getEntity(), from);
		    if (nDist <= ServerConstants.ENTITY_UPDATE_DIST) {
			cli.getNetClient().send(Packet7EntityMove.create(e));
		    } else if (oDist > ServerConstants.ENTITY_DISPOSE_DIST) {
			cli.getNetClient().send(
				Packet8EntityDispose.create(entity));
		    }
		}
	    }
	}
    }

    public Map<Integer, Entity> registeredEntities() {
	return Collections.unmodifiableMap(entityMap);
    }

    @Override
    public void entityLayerChange(int entity, int from, int to) {

    }

    @Override
    public void entityCreate(int entity) {
    }

    @Override
    public void entityDispose(int entity) {
    }

    public void clientMove(Client cli, Location from, Location to) {
	if (cli != null && cli.getEntity() != null
		&& cli.getNetClient() != null) {
	    for (Entity e : entityMap.values()) {
		if (e != null) {
		    int nDist = Location.dist(e, to);
		    int oDist = from != null ? Location.dist(e, from) : -1;
		    if (nDist <= ServerConstants.ENTITY_UPDATE_DIST) {
			if (from == null) {
			    cli.getNetClient()
				    .send(Packet9EntityData.create(e));
			} else {
			    cli.getNetClient()
				    .send(Packet7EntityMove.create(e));
			}
		    } else if (oDist > ServerConstants.ENTITY_DISPOSE_DIST) {
			cli.getNetClient().send(
				Packet8EntityDispose.create(e.getEntityID()));
		    }
		}
	    }
	}
    }
}
