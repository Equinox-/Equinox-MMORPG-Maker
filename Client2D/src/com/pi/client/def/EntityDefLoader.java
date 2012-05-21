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

	private EntityDefStorage getStorage(int defID) {
		Integer p = new Integer(defID);
		EntityDefStorage sS = map.get(p);
		if ((sS == null || (sS.def == null && !sS.empty)) && goodRequest(defID)) {
			loadQueue.add(defID);
			return null;
		}
		return sS;
	}

	public boolean isEmpty(int defID) {
		EntityDefStorage stor = getStorage(defID);
		return stor != null ? stor.empty : true;
	}

	public EntityDef getDef(int defID) {
		EntityDefStorage stor = getStorage(defID);
		return stor != null ? stor.def : null;
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
		if (defID != -1 && map.get(defID) == null && goodRequest(defID)) {
			Packet12EntityDefRequest packet = new Packet12EntityDefRequest();
			packet.defID = defID;
			client.getNetwork().send(packet);
			requestQueue.set(defID, System.currentTimeMillis());
		}
	}

	private boolean goodRequest(int defID) {
		return requestQueue.get(defID) == null
				|| requestQueue.get(defID) + requestExpiry < System
						.currentTimeMillis();
	}

	private static class EntityDefStorage {
		public boolean empty = false;
		public EntityDef def;
	}

	public ObjectHeap<EntityDefStorage> loadedMap() {
		return map;
	}
}
