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
import com.pi.common.database.io.SectorIO;

public class SectorWriter extends ClientThread {
    private Map<SectorLocation, WritableRequest> writeQueue = Collections
	    .synchronizedMap(new HashMap<SectorLocation, WritableRequest>());
    private Object syncObject = new Object();

    public SectorWriter(Client client) {
	super(client);
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
    protected void loop() {
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

    private static class WritableRequest {
	public Sector data;
	public long requestTime;
    }
}
