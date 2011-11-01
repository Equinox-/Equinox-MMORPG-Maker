package com.pi.client.world;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pi.client.Client;
import com.pi.client.database.Paths;
import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;
import com.pi.common.database.io.SectorIO;

public class SectorWriter extends Thread {
    private final Client client;
    private boolean running = true;
    private Map<SectorLocation, WritableRequest> writeQueue = Collections
	    .synchronizedMap(new HashMap<SectorLocation, WritableRequest>());
    private Object syncObject = new Object();

    public SectorWriter(Client client) {
	super(client.getThreadGroup(), null, "SectorWriter");
	this.client = client;
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
	while (running) {
	    doRequest();
	}
	client.getLog().fine("Killing Sector Writer Thread");
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
		    File fin = Paths.getSectorFile(oldestSector);
		    File dest = new File(fin.getAbsolutePath() + ".tmp");
		    SectorIO.write(dest, wr.data);
		    dest.renameTo(fin);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    public void dispose() {
	running = false;
	try {
	    join();
	} catch (InterruptedException e) {
	    e.printStackTrace(client.getLog().getErrorStream());
	    System.exit(0);
	}
    }

    private static class WritableRequest {
	public Sector data;
	public long requestTime;
    }
}
