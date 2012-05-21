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

public class SectorWriter extends ClientThread {
	private Map<SectorLocation, WritableRequest> writeQueue = Collections
			.synchronizedMap(new HashMap<SectorLocation, WritableRequest>());

	public SectorWriter(Client client) {
		super(client);
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
	protected void loop() {
		synchronized (mutex) {
			if (writeQueue.size() <= 0) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
				}
			} else {
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
						DatabaseIO.write(dest, wr.data);
						dest.renameTo(fin);
					} catch (IOException e) {
						client.getLog().printStackTrace(e);
					}
				}
			}
		}
	}

	private static class WritableRequest {
		public Sector data;
		public long requestTime;
	}
}
