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

public class ClientEntityManager {
    private final Map<Integer, Entity> entityMap = Collections
	    .synchronizedMap(new HashMap<Integer, Entity>());
    private final Client client;
    private int localEntityID = 0;

    public ClientEntityManager(Client client) {
	this.client = client;
    }

    public boolean isEntityRegistered(int id) {
	return entityMap.containsKey(id);
    }

    public void setLocalEntityID(int id) {
	this.localEntityID = id;
    }

    public ClientEntity getLocalEntity() {
	return getEntity(localEntityID);
    }

    public boolean saveEntity(Entity eI) {
	ClientEntity e;
	if (eI instanceof ClientEntity) {
	    e = (ClientEntity) eI;
	} else {
	    e = new ClientEntity(eI);
	}
	int id = e.getEntityID();
	if (id == -1) {
	    while (true) {
		if (!entityMap.containsKey(id))
		    break;
		id++;
	    }
	    if (e.setEntityID(id)) {
		entityMap.put(id, e);
		return true;
	    }
	} else {
	    entityMap.put(e.getEntityID(), e);
	    return true;
	}
	return false;
    }

    public boolean deRegisterEntity(int id) {
	return entityMap.remove(id) != null;
    }

    public List<ClientEntity> getEntitiesInSector(SectorLocation loc) {
	List<ClientEntity> sector = new ArrayList<ClientEntity>();
	for (Entity e : entityMap.values()) {
	    if (loc.containsLocation(e)) {
		sector.add((ClientEntity) e);
	    }
	}
	return sector;
    }

    public List<ClientEntity> getEntitiesWithin(Location l, int maxDist) {
	List<ClientEntity> entities = new ArrayList<ClientEntity>();
	for (Entity e : entityMap.values()) {
	    if (Location.dist(l, e) <= maxDist) {
		entities.add((ClientEntity) e);
	    }
	}
	return entities;
    }

    public ClientEntity getEntity(int id) {
	return (ClientEntity) entityMap.get(id);
    }

    public Map<Integer, Entity> registeredEntities() {
	return Collections.unmodifiableMap(entityMap);
    }

    public Client getClient() {
	return client;
    }
}
