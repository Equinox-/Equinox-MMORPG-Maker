package com.pi.server.world;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;
import com.pi.common.database.io.DatabaseIO;
import com.pi.server.Server;
import com.pi.server.ServerThread;
import com.pi.server.database.Paths;

public class SectorWriter extends ServerThread {
    private Map<SectorLocation, WritableRequest> writeQueue = Collections
	    .synchronizedMap(new HashMap<SectorLocation, WritableRequest>());

    public SectorWriter(Server server) {
	super(server);
	mutex = new Object();
	start();
    }

    public void writeSector(Sector sec) {
	synchronized (mutex) {
	    WritableRequest write = new WritableRequest();
	    write.data = sec;
	    write.requestTime = System.currentTimeMillis();
	    writeQueue.put(sec.getSectorLocation(), write);
	    mutex.notify();
	}
    }

    @Override
    public void loop() {
	synchronized (mutex) {
	    if (writeQueue.size() <= 0) {
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
		DatabaseIO.write(Paths.getSectorFile(oldestSector), wr.data);
	    } catch (IOException e) {
		server.getLog().printStackTrace(e);
	    }
	}
    }

    private static class WritableRequest {
	public Sector data;
	public long requestTime;
    }
}
