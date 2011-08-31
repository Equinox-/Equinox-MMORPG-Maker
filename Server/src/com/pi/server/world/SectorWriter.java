package com.pi.server.world;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;
import com.pi.common.database.io.SectorIO;
import com.pi.server.Server;
import com.pi.server.database.Paths;

public class SectorWriter extends Thread {
    private final Server server;
    private boolean running = true;
    private Map<SectorLocation, WritableRequest> writeQueue = new HashMap<SectorLocation, WritableRequest>();
    private Object syncObject = new Object();

    public SectorWriter(Server server) {
	super(server.getThreadGroup(), null,"SectorWriter");
	this.server = server;
	start();
    }

    public void writeSector(Sector sec) {
	synchronized (syncObject) {
	    WritableRequest write = new WritableRequest();
	    write.data = sec;
	    write.requestTime = System.currentTimeMillis();
	    writeQueue.put(sec.getSectorLocation(), write);
	}
    }

    @Override
    public void run() {
	server.getLog().finer("Started Sector Writer Thread");
	while (running) {
	    doRequest();
	}
	server.getLog().finer("Killed Sector Writer Thread");
    }

    private void doRequest() {
	synchronized (syncObject) {
	    long oldestTime = Long.MAX_VALUE;
	    SectorLocation oldestSector = null;
	    for (SectorLocation i : writeQueue.keySet()) {
		long requestTime = writeQueue.get(i).requestTime;
		if (oldestTime > requestTime) {
		    oldestTime = requestTime;
		    oldestSector = i;
		}
	    }
	    if (oldestSector != null) {
		WritableRequest wr = writeQueue.remove(oldestSector);
		try {
		    SectorIO.write(Paths.getSectorFile(oldestSector), wr.data);
		} catch (IOException e) {
		    e.printStackTrace(server.getLog().getErrorStream());
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

    private static class WritableRequest {
	public Sector data;
	public long requestTime;
    }
}
