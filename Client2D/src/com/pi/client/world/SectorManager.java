package com.pi.client.world;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pi.client.Client;
import com.pi.client.ClientThread;
import com.pi.client.database.Paths;
import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;
import com.pi.common.database.io.DatabaseIO;
import com.pi.common.net.packet.Packet5SectorRequest;

public class SectorManager extends ClientThread {
    public final static int sectorExpiry = 60000; // 1 Minute
    public final static int serverRequestExpiry = 60000; // 30 seconds

    private Map<SectorLocation, Long> loadQueue = Collections
	    .synchronizedMap(new HashMap<SectorLocation, Long>());
    private Map<SectorLocation, SectorStorage> map = Collections
	    .synchronizedMap(new HashMap<SectorLocation, SectorStorage>());
    private Map<SectorLocation, Long> sentRequests = Collections
	    .synchronizedMap(new HashMap<SectorLocation, Long>());
    private Object syncObject = new Object();

    public Map<SectorLocation, SectorStorage> loadedMap() {
	return Collections.unmodifiableMap(map);
    }

    public SectorManager(Client client) {
	super(client);
	start();
    }

    public Sector getSector(int x, int y, int z) {
	synchronized (syncObject) {
	    SectorLocation p = new SectorLocation(x, y, z);
	    SectorStorage sS = map.get(p);
	    if (sS == null || (sS.data == null && !sS.empty)) {
		loadQueue.put(p, System.currentTimeMillis());
		return null;
	    }
	    return sS != null ? sS.data : null;
	}
    }

    public void setSector(Sector sector) {
	synchronized (syncObject) {
	    SectorStorage sec = map.get(sector.getSectorLocation());
	    if (sec == null)
		sec = new SectorStorage();
	    sec.lastUsed = System.currentTimeMillis();
	    sec.data = sector;
	    map.put(sector.getSectorLocation(), sec);
	    client.getWorld().getSectorWriter().writeSector(sector);
	    client.getLog().info(
		    "Loaded client sector: "
			    + sector.getSectorLocation().toString());
	}
    }

    @Override
    public void loop() {
	doRequest();
	removeExpired();
    }

    private void removeExpired() {
	synchronized (syncObject) {
	    for (SectorLocation i : map.keySet()) {
		if (System.currentTimeMillis() - map.get(i).lastUsed > sectorExpiry) {
		    map.remove(i);
		}
	    }
	}
    }

    private void doRequest() {
	synchronized (syncObject) {
	    long oldestTime = Long.MAX_VALUE;
	    SectorLocation oldestSector = null;
	    for (SectorLocation i : loadQueue.keySet()) {
		long requestTime = loadQueue.get(i);
		if (System.currentTimeMillis() - requestTime > sectorExpiry) {
		    loadQueue.remove(i);
		} else {
		    if (oldestTime > requestTime) {
			oldestTime = requestTime;
			oldestSector = i;
		    }
		}
	    }
	    if (oldestSector != null) {
		loadQueue.remove(oldestSector);
		SectorStorage sX = new SectorStorage();
		File f = Paths.getSectorFile(oldestSector);
		int revision = -1;
		if (f.exists()) {
		    try {
			sX.data = (Sector) DatabaseIO.read(f,Sector.class);
			revision = sX.data.getRevision();
		    } catch (IOException e) {
			client.getLog().severe(
				"Corrupted sector cache: "
					+ oldestSector.toString());
			f.delete();
		    }
		}
		if (client.isNetworkConnected()) {
		    if (sentRequests.get(oldestSector) == null
			    || sentRequests.get(oldestSector).longValue()
				    + serverRequestExpiry < System
					.currentTimeMillis()) {
			sentRequests.put(oldestSector,
				System.currentTimeMillis());
			Packet5SectorRequest pack = new Packet5SectorRequest();
			pack.baseX = oldestSector.x;
			pack.baseY = oldestSector.plane;
			pack.baseZ = oldestSector.z;
			pack.revision = revision;
			client.getNetwork().send(pack);
			sX.lastUsed = System.currentTimeMillis();
			map.put(oldestSector, sX);
		    }
		}
	    }
	}
    }

    public void flagSectorAsBlack(SectorLocation p) {
	if (!map.containsKey(p))
	    map.put(p, new SectorStorage());
	map.get(p).empty = true;
    }

    public static class SectorStorage {
	public long lastUsed;
	public Sector data;
	public boolean empty;
    }
}
