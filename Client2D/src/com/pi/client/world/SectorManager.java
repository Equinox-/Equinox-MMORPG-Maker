package com.pi.client.world;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pi.client.Client;
import com.pi.client.database.Paths;
import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;
import com.pi.common.database.io.SectorIO;
import com.pi.common.net.packet.Packet5SectorRequest;

public class SectorManager extends Thread {
	public final static int sectorExpiry = 60000; // 1 Minute
	public final static int serverRequestExpiry = 30000; // 30 seconds
	private List<SectorLocation> blankSectors = new ArrayList<SectorLocation>();
	private final Client client;
	private boolean running = true;

	private Map<SectorLocation, Long> loadQueue = new HashMap<SectorLocation, Long>();
	private Map<SectorLocation, SectorStorage> map = new HashMap<SectorLocation, SectorStorage>();
	private Map<SectorLocation, Long> sentRequests = new HashMap<SectorLocation, Long>();
	private Object syncObject = new Object();

	public Map<SectorLocation, SectorStorage> loadedMap() {
		return Collections.unmodifiableMap(map);
	}

	public SectorManager(Client client) {
		super(client.getThreadGroup(), null, "SectorManager");
		this.client = client;
		start();
	}

	public Sector getSector(int x, int y, int z) {
		synchronized (syncObject) {
			SectorLocation p = new SectorLocation(x, y, z);
			SectorStorage sS = map.get(p);
			if ((sS == null || sS.data == null) && !blankSectors.contains(p)) {
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
			client.getWorld().getSectorWriter().writeSector(sector);
		}
	}

	@Override
	public void run() {
		while (running) {
			doRequest();
			removeExpired();
		}
		client.getLog().fine("Killing Sector Manager Thread");
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
						sX.data = SectorIO.read(f);
						revision = sX.data.getRevision();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (client.getNetwork() != null
						&& client.getNetwork().getSocket() != null
						&& client.getNetwork().getSocket().isConnected()) {
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

	public void dispose() {
		running = false;
		try {
			join();
		} catch (InterruptedException e) {
			e.printStackTrace(client.getLog().getErrorStream());
			System.exit(0);
		}
	}

	public void flagSectorAsBlack(SectorLocation p) {
		if (!blankSectors.contains(p))
			blankSectors.add(p);
	}

	public static class SectorStorage {
		public long lastUsed;
		public Sector data;
	}
}
