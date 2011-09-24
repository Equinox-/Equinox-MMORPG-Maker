package com.pi.server.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;
import com.pi.common.database.io.SectorIO;
import com.pi.common.net.client.NetClient;
import com.pi.common.net.packet.Packet4Sector;
import com.pi.common.net.packet.Packet5SectorRequest;
import com.pi.common.net.packet.Packet6BlankSector;
import com.pi.server.Server;
import com.pi.server.database.Paths;

public class SectorManager extends Thread {
    public final static int sectorExpiry = 300000; // 5 Minutes
    private final Server server;
    private boolean running = true;

    private Map<SectorLocation, Long> loadQueue = Collections
	    .synchronizedMap(new HashMap<SectorLocation, Long>());
    private Map<SectorLocation, SectorStorage> map = Collections
	    .synchronizedMap(new HashMap<SectorLocation, SectorStorage>());
    private List<ClientSectorRequest> requests = Collections
	    .synchronizedList(new ArrayList<ClientSectorRequest>());
    private Object syncObject = new Object();

    public SectorManager(Server server) {
	super(server.getThreadGroup(), null, "SectorManager");
	this.server = server;
	start();
    }

    public void requestSector(int clientID, Packet5SectorRequest req) {
	synchronized (syncObject) {
	    Sector sec = getSector(req.baseX, req.baseY, req.baseZ);
	    if (sec != null) {
		if (sec.getRevision() != req.revision) {
		    Packet4Sector packet = new Packet4Sector();
		    packet.sector = sec;
		    NetClient netClient = server.getNetwork().getClientMap()
			    .get(clientID);
		    netClient.send(packet);
		}
	    } else {
		ClientSectorRequest request = new ClientSectorRequest(clientID,
			req);
		requests.remove(request);
		requests.add(request);
	    }
	}
    }

    public Sector getSector(int x, int y, int z) {
	synchronized (syncObject) {
	    SectorLocation p = new SectorLocation(x, y, z);
	    SectorStorage sS = map.get(p);
	    if (sS == null || sS.data == null) {
		loadQueue.put(p, System.currentTimeMillis());
		return null;
	    }
	    return sS.data;
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
	    server.getWorld().getSectorWriter().writeSector(sector);
	}
    }

    @Override
    public void run() {
	server.getLog().finer("Started Sector Manager Thread");
	while (running) {
	    doRequest();
	    removeExpired();
	}
	server.getLog().finer("Killed Sector Manager Thread");
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

    private Iterator<ClientSectorRequest> getTypeIterator(final int x,
	    final int y, final int z) {
	return new Iterator<ClientSectorRequest>() {
	    int idx = 0;

	    @Override
	    public boolean hasNext() {
		int idx2 = idx;
		while (idx < requests.size()) {
		    ClientSectorRequest r = requests.get(idx2);
		    idx2++;
		    if (r.baseX == x && r.baseY == y && r.baseZ == z) {
			return true;
		    }
		}
		return false;
	    }

	    @Override
	    public ClientSectorRequest next() {
		while (idx < requests.size()) {
		    ClientSectorRequest r = requests.get(idx);
		    idx++;
		    if (r.baseX == x && r.baseY == y && r.baseZ == z) {
			return r;
		    }
		}
		return null;
	    }

	    @Override
	    public void remove() {
	    }
	};
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
		try {
		    sX.data = SectorIO.read(Paths.getSectorFile(oldestSector));
		} catch (FileNotFoundException e) {
		    Packet6BlankSector blankPack = null;
		    Iterator<ClientSectorRequest> myReqs = getTypeIterator(
			    oldestSector.x, oldestSector.plane, oldestSector.z);
		    if (myReqs.hasNext()) {
			if (blankPack == null) {
			    blankPack = new Packet6BlankSector();
			    blankPack.baseX = oldestSector.x;
			    blankPack.baseY = oldestSector.plane;
			    blankPack.baseZ = oldestSector.z;
			}
		    }
		    while (myReqs.hasNext()) {
			ClientSectorRequest req = myReqs.next();
			if (req != null) {
			    synchronized (syncObject) {
				NetClient nC = server.getNetwork().getClient(
					req.clientId);
				if (nC != null && nC.isConnected()) {
				    nC.send(blankPack);
				    server.getLog().fine(
					    "Notifing client " + req.clientId
						    + " of empty sector "
						    + blankPack.baseX + ","
						    + blankPack.baseY);
				}
				requests.remove(req);
			    }
			}		    }
		} catch (IOException e) {
		    e.printStackTrace(server.getLog().getErrorStream());
		}
		sX.lastUsed = System.currentTimeMillis();
		map.put(oldestSector, sX);
		server.getLog().info("Loaded: " + oldestSector.toString());
		// Go through the requests, grabbing the correct ones
		Packet4Sector secPack = null;
		for (ClientSectorRequest req : requests) {
		    if (req.baseX == oldestSector.x
			    && req.baseY == oldestSector.plane
			    && req.baseZ == oldestSector.z) {
			if (secPack == null) {
			    secPack = new Packet4Sector();
			    secPack.sector = sX.data;
			}
			NetClient nC = server.getNetwork().getClient(
				req.clientId);
			if (nC != null && nC.isConnected()) {
			    nC.send(secPack);
			    server.getLog().fine(
				    "Sending sector " + oldestSector.x + ","
					    + oldestSector.plane
					    + " to client " + req.clientId);
			}
			requests.remove(req);
		    }
		}
	    }
	}
    }

    public void dispose() {
	running = false;
	try {
	    join();
	} catch (InterruptedException e) {
	    e.printStackTrace(server.getLog().getErrorStream());
	    System.exit(0);
	}
    }

    public static class SectorStorage {
	public long lastUsed;
	public Sector data;
    }

    public static class ClientSectorRequest {
	public int clientId;
	public int revision;
	public int baseX, baseY, baseZ;

	public ClientSectorRequest(int client, Packet5SectorRequest req) {
	    this.clientId = client;
	    this.revision = req.revision;
	    this.baseX = req.baseX;
	    this.baseY = req.baseY;
	    this.baseZ = req.baseZ;
	}

	@Override
	public boolean equals(Object o) {
	    if (o instanceof ClientSectorRequest) {
		ClientSectorRequest req = (ClientSectorRequest) o;
		return this.clientId == req.clientId && this.baseX == req.baseX
			&& this.baseY == req.baseY && this.baseZ == req.baseZ;
	    }
	    return false;
	}
    }

    public Map<SectorLocation, SectorStorage> loadedMap() {
	return Collections.unmodifiableMap(map);
    }
}
