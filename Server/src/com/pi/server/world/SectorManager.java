package com.pi.server.world;

import java.awt.Point;
import java.io.IOException;
import java.util.*;

import com.pi.common.database.Sector;
import com.pi.common.database.io.SectorIO;
import com.pi.common.net.client.NetClient;
import com.pi.common.net.packet.Packet4Sector;
import com.pi.server.Server;
import com.pi.server.database.Paths;
import com.pi.server.net.client.NetServerClient;

public class SectorManager extends Thread {
    public final static int sectorExpiry = 300000; // 5 Minutes
    private final Server server;
    private boolean running = true;

    private Map<Point, Long> loadQueue = new HashMap<Point, Long>();
    private Map<Point, SectorStorage> map = new HashMap<Point, SectorStorage>();

    public SectorManager(Server server) {
	super("SectorManager");
	this.server = server;
	start();
    }

    public synchronized Sector getSector(int x, int y) {
	Point p = new Point(x, y);
	SectorStorage sS = map.get(p);
	if (sS == null || sS.data == null) {
	    loadQueue.put(p, System.currentTimeMillis());
	    return null;
	}
	return sS.data;
    }

    public synchronized void setSector(Sector sector) {
	SectorStorage sec = map.get(sector.getSectorLocation());
	if (sec == null)
	    sec = new SectorStorage();
	sec.lastUsed = System.currentTimeMillis();
	sec.data = sector;
	map.put(sector.getSectorLocation(), sec);
	server.getWorld().getSectorWriter().writeSector(sector);
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
	for (Point i : map.keySet()) {
	    if (System.currentTimeMillis() - map.get(i).lastUsed > sectorExpiry) {
		map.remove(i);
	    }
	}
    }

    private void doRequest() {
	long oldestTime = Long.MAX_VALUE;
	Point oldestSector = null;
	for (Point i : loadQueue.keySet()) {
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
		sX.data = SectorIO.read(Paths.getSectorFile(oldestSector.x,
			oldestSector.y));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    sX.lastUsed = System.currentTimeMillis();
	    map.put(oldestSector, sX);
	    server.getLog().info("Loaded: " + oldestSector.toString());
	}
    }

    public void dispose() {
	running = false;
	try {
	    join();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	    System.exit(0);
	}
    }

    private static class SectorStorage {
	public long lastUsed;
	public Sector data;
    }
}
