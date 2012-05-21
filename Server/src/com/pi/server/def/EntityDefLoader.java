package com.pi.server.def;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import com.pi.common.database.def.EntityDef;
import com.pi.common.database.io.DatabaseIO;
import com.pi.common.game.ObjectHeap;
import com.pi.common.net.packet.Packet12EntityDefRequest;
import com.pi.common.net.packet.Packet13EntityDef;
import com.pi.server.Server;
import com.pi.server.ServerThread;
import com.pi.server.database.Paths;

public class EntityDefLoader extends ServerThread {
	private LinkedList<Integer> loadQueue = new LinkedList<Integer>();
	private ObjectHeap<EntityDefStorage> map = new ObjectHeap<EntityDefStorage>();

	public EntityDefLoader(Server server) {
		super(server);
		mutex = new Object();
		start();
	}

	public void requestDef(int clientID, Packet12EntityDefRequest req) {
		synchronized (mutex) {
			EntityDefStorage sec = getEntityDef(req.defID);
			if (sec != null && (sec.data != null || sec.empty)) {
				Packet13EntityDef packet = new Packet13EntityDef();
				packet.def = sec.empty ? null : sec.data;
				packet.entityID = req.defID;
				server.getClientManager().getClient(clientID).getNetClient()
						.send(packet);
			} else {
				/*
				 * ClientDefRequest request = new ClientDefRequest(clientID,
				 * req); requests.remove(request); requests.add(request);
				 */
			}
		}
	}

	public EntityDef getDef(int def) {
		EntityDefStorage ss = getEntityDef(def);
		return ss != null ? ss.data : null;
	}

	public boolean isEmptyDef(int def) {
		EntityDefStorage ss = getEntityDef(def);
		return ss != null ? ss.empty : false;
	}

	private EntityDefStorage getEntityDef(int defID) {
		synchronized (mutex) {
			Integer p = new Integer(defID);
			EntityDefStorage sS = map.get(p);
			if (sS == null || (sS.data == null && !sS.empty)) {
				loadQueue.addLast(p);
				mutex.notify();
				return null;
			}
			return sS;
		}
	}

	public void setDef(EntityDef eDef) {
		synchronized (mutex) {
			EntityDefStorage sec = map.get(eDef.getDefID());
			if (sec == null)
				sec = new EntityDefStorage();
			sec.data = eDef;
			map.set(eDef.getDefID(), sec);
			// TODO server.getWorld().getDefWriter().writeDef(eDef);
		}
	}

	@Override
	public void loop() {
		synchronized (mutex) {
			if (loadQueue.size() <= 0) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
				}
			} else {
				doRequest();
			}
		}
	}

	private void doRequest() {
		Integer oldestDef = loadQueue.removeFirst();
		EntityDefStorage sX = map.get(oldestDef);
		if (sX == null || (sX.data == null && !sX.empty)) {
			if (sX == null)
				sX = new EntityDefStorage();
			try {
				sX.data = (EntityDef) DatabaseIO.read(
						Paths.getEntityDef(oldestDef), EntityDef.class);
				sX.empty = false;
				map.set(oldestDef, sX);
				server.getLog().info("Loaded: " + oldestDef.toString());
			} catch (FileNotFoundException e) {
				sX.data = null;
				sX.empty = true;
				map.set(oldestDef, sX);
				server.getLog().info(
						"Flagged as empty: " + oldestDef.toString());
			} catch (IOException e) {
				server.getLog().printStackTrace(e);
			}
		}
	}

	private static class EntityDefStorage {
		public EntityDef data;
		public boolean empty = false;
	}

	public ObjectHeap<EntityDefStorage> loadedMap() {
		return map;
	}
}
