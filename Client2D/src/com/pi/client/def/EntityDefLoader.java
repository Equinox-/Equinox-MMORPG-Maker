package com.pi.client.def;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pi.client.Client;
import com.pi.common.database.def.EntityDef;
import com.pi.common.net.packet.Packet12EntityDefRequest;

public class EntityDefLoader {
    private final Client client;
    private Map<Integer, EntityDefStorage> map = Collections
	    .synchronizedMap(new HashMap<Integer, EntityDefStorage>());
    private Object mutex = new Object();

    public EntityDefLoader(Client client) {
	this.client = client;
    }

    public EntityDef getDef(int defID) {
	synchronized (mutex) {
	    Integer p = new Integer(defID);
	    EntityDefStorage sS = map.get(p);
	    if (sS == null || (sS.def == null && !sS.empty)) {
		Packet12EntityDefRequest packet = new Packet12EntityDefRequest();
		packet.defID = defID;
		client.getNetwork().send(packet);
		return null;
	    }
	    return sS.def;
	}
    }

    public boolean isEmpty(int defID) {
	synchronized (mutex) {
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
    }

    public void setDef(int entityID, EntityDef eDef) {
	synchronized (mutex) {
	    EntityDefStorage str = map.get(entityID);
	    if (str == null)
		str = new EntityDefStorage();
	    str.def = eDef;
	    str.empty = eDef == null;
	    map.put(entityID, str);
	}
    }

    private static class EntityDefStorage {
	public boolean empty = false;
	public EntityDef def;
    }

    public Map<Integer, EntityDefStorage> loadedMap() {
	synchronized (mutex) {
	    return Collections.unmodifiableMap(map);
	}
    }
}
