package com.pi.client.def;

import java.util.Vector;

import com.pi.client.Client;
import com.pi.common.database.def.EntityDef;
import com.pi.common.game.ObjectHeap;
import com.pi.common.net.packet.Packet12EntityDefRequest;

public class EntityDefLoader {
    private static final long requestExpiry = 2500;
    private final Client client;
    private ObjectHeap<EntityDefStorage> map = new ObjectHeap<EntityDefStorage>();
    private Vector<Integer> loadQueue = new Vector<Integer>();
    private ObjectHeap<Long> requestQueue = new ObjectHeap<Long>();

    public EntityDefLoader(Client client) {
	this.client = client;
    }

    public EntityDef getDef(int defID) {
	Integer p = new Integer(defID);
	EntityDefStorage sS = map.get(p);
	if (sS == null || (sS.def == null && !sS.empty)) {
	    loadQueue.add(defID);
	    return null;
	}
	return sS.def;
    }

    public boolean isEmpty(int defID) {
	Integer p = new Integer(defID);
	EntityDefStorage sS = map.get(p);
	if (sS == null || (sS.def == null && !sS.empty)) {
	    Packet12EntityDefRequest packet = new Packet12EntityDefRequest();
	    packet.defID = defID;
	    client.getNetwork().send(packet);
	    return false;
	}
	return sS.empty;
    }

    public void setDef(int entityID, EntityDef eDef) {
	EntityDefStorage str = map.get(entityID);
	if (str == null)
	    str = new EntityDefStorage();
	str.def = eDef;
	str.empty = eDef == null;
	map.set(entityID, str);
	requestQueue.remove(entityID);
    }

    public void loadLoop() {
	int defID = loadQueue.size() > 0 ? loadQueue.remove(0) : -1;
	if (defID != -1
		&& map.get(defID) == null
		&& (requestQueue.get(defID) == null || requestQueue.get(defID)
			+ requestExpiry > System.currentTimeMillis())) {
	    Packet12EntityDefRequest packet = new Packet12EntityDefRequest();
	    packet.defID = defID;
	    client.getNetwork().send(packet);
	    requestQueue.set(defID, System.currentTimeMillis());
	}
    }

    private static class EntityDefStorage {
	public boolean empty = false;
	public EntityDef def;
    }

    public ObjectHeap<EntityDefStorage> loadedMap() {
	return map;
    }
}
