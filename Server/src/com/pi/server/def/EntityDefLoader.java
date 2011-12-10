package com.pi.server.def;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pi.common.database.def.EntityDef;
import com.pi.common.net.packet.Packet12EntityDefRequest;
import com.pi.common.net.packet.Packet13EntityDef;
import com.pi.server.Server;
import com.pi.server.ServerThread;
import com.pi.server.database.Paths;
import com.pi.server.database.io.EntityDefIO;

public class EntityDefLoader extends ServerThread {
    private Map<Integer, Long> loadQueue = Collections
	    .synchronizedMap(new HashMap<Integer, Long>());
    private Map<Integer, EntityDefStorage> map = Collections
	    .synchronizedMap(new HashMap<Integer, EntityDefStorage>());
    private Object mutex = new Object();

    public EntityDefLoader(Server server) {
	super(server);
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
		loadQueue.put(p, System.currentTimeMillis());
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
	    map.put(eDef.getDefID(), sec);
	    // TODO server.getWorld().getDefWriter().writeDef(eDef);
	}
    }

    @Override
    public void loop() {
	doRequest();
    }

    private void doRequest() {
	synchronized (mutex) {
	    long oldestTime = Long.MAX_VALUE;
	    Integer oldestDef = null;
	    for (Integer i : loadQueue.keySet()) {
		long requestTime = loadQueue.get(i);
		EntityDefStorage sCurr = map.get(i);
		if (sCurr == null || (sCurr.data != null && !sCurr.empty)) {
		    if (oldestTime > requestTime) {
			oldestTime = requestTime;
			oldestDef = i;
		    }
		}
	    }
	    if (oldestDef != null) {
		loadQueue.remove(oldestDef);
		EntityDefStorage sX = map.get(oldestDef);
		if (sX == null || (sX.data == null && !sX.empty)) {
		    if (sX == null)
			sX = new EntityDefStorage();
		    try {
			sX.data = EntityDefIO.read(Paths
				.getEntityDef(oldestDef));
			sX.empty = false;
			map.put(oldestDef, sX);
			server.getLog().info("Loaded: " + oldestDef.toString());
		    } catch (FileNotFoundException e) {
			sX.data = null;
			sX.empty = true;
			map.put(oldestDef, sX);
			server.getLog().info(
				"Flagged as empty: " + oldestDef.toString());
		    } catch (IOException e) {
			e.printStackTrace(server.getLog().getErrorStream());
		    }
		}
	    }
	}
    }

    private static class EntityDefStorage {
	public EntityDef data;
	public boolean empty = false;
    }

    public Map<Integer, EntityDefStorage> loadedMap() {
	synchronized (mutex) {
	    return Collections.unmodifiableMap(map);
	}
    }
}
