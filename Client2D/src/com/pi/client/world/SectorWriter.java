package com.pi.client.world;

import java.awt.Point;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pi.client.Client;
import com.pi.client.database.Paths;
import com.pi.common.database.Sector;
import com.pi.common.database.io.SectorIO;

public class SectorWriter extends Thread {
    private final Client client;
    private boolean running = true;
    private Map<Point, WritableRequest> writeQueue = new HashMap<Point, WritableRequest>();

    public SectorWriter(Client client) {
	super("SectorWriter");
	this.client = client;
	start();
    }

    public synchronized void writeSector(Sector sec) {
	WritableRequest write = new WritableRequest();
	write.data = sec;
	write.requestTime = System.currentTimeMillis();
	writeQueue.put(sec.getSectorLocation(), write);
    }

    @Override
    public void run() {
	while (running) {
	    doRequest();
	}
	client.getLog().fine("Killing Sector Writer Thread");
    }

    private void doRequest() {
	long oldestTime = Long.MAX_VALUE;
	Point oldestSector = null;
	for (Point i : writeQueue.keySet()) {
	    long requestTime = writeQueue.get(i).requestTime;
	    if (oldestTime > requestTime) {
		oldestTime = requestTime;
		oldestSector = i;
	    }
	}
	if (oldestSector != null) {
	    WritableRequest wr = writeQueue.remove(oldestSector);
	    try {
		SectorIO.write(
			Paths.getSectorFile(oldestSector.x, oldestSector.y),
			wr.data);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
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

    private static class WritableRequest {
	public Sector data;
	public long requestTime;
    }
}
