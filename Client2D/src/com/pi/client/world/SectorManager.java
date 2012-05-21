package com.pi.client.world;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
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

	private LinkedList<SectorLocation> loadQueue = new LinkedList<SectorLocation>();
	private Hashtable<SectorLocation, SectorStorage> map = new Hashtable<SectorLocation, SectorStorage>();
	private Hashtable<SectorLocation, Long> sentRequests = new Hashtable<SectorLocation, Long>();

	public Map<SectorLocation, SectorStorage> loadedMap() {
		return Collections.unmodifiableMap(map);
	}

	public SectorManager(Client client) {
		super(client);
		mutex = new Object();
		start();
	}

	public Sector getSector(int x, int y, int z) {
		synchronized (mutex) {
			SectorLocation p = new SectorLocation(x, y, z);
			SectorStorage sS = map.get(p);
			if (sS == null || (sS.data == null && !sS.empty)) {
				if (!loadQueue.contains(p)) { // TODO FASTER
					loadQueue.addLast(p);
					mutex.notify();
				}
				return null;
			}
			return sS != null ? sS.data : null;
		}
	}

	public void setSector(Sector sector) {
		synchronized (mutex) {
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
		synchronized (mutex) {
			if (loadQueue.size() <= 0) {
				try {
					mutex.wait();
				} catch (InterruptedException e) {
				}
			} else {
				doRequest();
				removeExpired();
			}
		}
	}

	private void removeExpired() {
		for (SectorLocation i : map.keySet()) {
			if (System.currentTimeMillis() - map.get(i).lastUsed > sectorExpiry) {
				map.remove(i);
			}
		}
	}

	private void doRequest() {
		SectorLocation oldestSector = loadQueue.removeFirst();
		if (oldestSector != null) {
			SectorStorage sX = new SectorStorage();
			File f = Paths.getSectorFile(oldestSector);
			int revision = -1;
			if (f.exists()) {
				try {
					sX.data = (Sector) DatabaseIO.read(f, Sector.class);
					revision = sX.data.getRevision();
				} catch (IOException e) {
					client.getLog().severe(
							"Corrupted sector cache: "
									+ oldestSector.toString());
					f.delete();
				}
			} else {
				sX.empty = true;
			}
			map.put(oldestSector, sX);
			if (client.isNetworkConnected()) {
				if (sentRequests.get(oldestSector) == null
						|| sentRequests.get(oldestSector).longValue()
								+ serverRequestExpiry < System
									.currentTimeMillis()) {
					sentRequests.put(oldestSector, System.currentTimeMillis());
					Packet5SectorRequest pack = new Packet5SectorRequest();
					pack.baseX = oldestSector.x;
					pack.baseY = oldestSector.plane;
					pack.baseZ = oldestSector.z;
					pack.revision = revision;
					client.getNetwork().send(pack);
					sX.lastUsed = System.currentTimeMillis();
				}
			}
		}
	}

	public void flagSectorAsBlack(SectorLocation p) {
		SectorStorage ss = map.get(p);
		if (ss == null) {
			ss = new SectorStorage();
			ss.empty = true;
			map.put(p, ss);
		} else {
			ss.empty = true;
		}
	}

	public static class SectorStorage {
		public long lastUsed;
		public Sector data;
		public boolean empty;
		public boolean requested = false;
	}
}
