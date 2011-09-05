package com.pi.server;

import java.util.*;

import com.pi.common.database.*;

public class ServerEntityManager {
    private final Map<Integer, Entity> entityMap = new HashMap<Integer, Entity>();

    public boolean registerEntity(Entity e) {
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
}
