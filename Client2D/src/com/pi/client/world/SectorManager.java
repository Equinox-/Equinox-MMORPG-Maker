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

public class SectorManager extends ClientThread implements
		com.pi.common.world.SectorManager {
	public static final int sectorExpiry = 60000; // 1 Minute
	public static final int serverRequestExpiry = 60000; // 30 seconds

	private LinkedList<SectorLocation> loadQueue =
			new LinkedList<SectorLocation>();
	private Hashtable<SectorLocation, SectorStorage> map =
			new Hashtable<SectorLocation, SectorStorage>();
	private Hashtable<SectorLocation, Long> sentRequests =
			new Hashtable<SectorLocation, Long>();

	public SectorManager(final Client client) {
		super(client);
		createMutex();
		start();
	}

	@Override
	public final boolean isEmptySector(final int x, final int y,
			final int z) {
		SectorStorage ss = getSectorStorage(x, y, z);
		return ss == null || ss.empty;
	}

	@Override
	public final Sector getSector(final int x, final int y,
			final int z) {
		SectorStorage ss = getSectorStorage(x, y, z);
		if (ss != null) {
			return ss.data;
		} else {
			return null;
		}
	}

	public final void setSector(final Sector sector) {
		synchronized (getMutex()) {
			SectorStorage sec =
					map.get(sector.getSectorLocation());
			if (sec == null) {
				sec = new SectorStorage();
			}
			sec.lastUsed = System.currentTimeMillis();
			sec.data = sector;
			map.put(sector.getSectorLocation(), sec);
		}
		// Write Sector:
		try {
			File fin =
					Paths.getSectorFile(sector.getSectorX(),
							sector.getSectorY(),
							sector.getSectorZ());
			File dest = new File(fin.getAbsolutePath() + ".tmp");
			DatabaseIO.write(dest, sector);
			dest.renameTo(fin);
		} catch (IOException e) {
			getClient().getLog().printStackTrace(e);
		}

		getClient().getLog().info(
				"Loaded client sector: "
						+ sector.getSectorLocation().toString());
	}

	@Override
	public final void loop() {
		synchronized (getMutex()) {
			if (loadQueue.size() <= 0) {
				try {
					getMutex().wait();
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
					sX.data =
							(Sector) DatabaseIO.read(f,
									Sector.class);
					revision = sX.data.getRevision();
				} catch (IOException e) {
					getClient().getLog().severe(
							"Corrupted sector cache: "
									+ oldestSector.toString());
					f.delete();
				}
			} else {
				sX.empty = true;
			}
			map.put(oldestSector, sX);
			if (getClient().isNetworkConnected()) {
				if (sentRequests.get(oldestSector) == null
						|| sentRequests.get(oldestSector)
								.longValue()
								+ serverRequestExpiry < System
									.currentTimeMillis()) {
					sentRequests.put(oldestSector,
							System.currentTimeMillis());
					Packet5SectorRequest pack =
							new Packet5SectorRequest();
					pack.baseX = oldestSector.x;
					pack.baseY = oldestSector.plane;
					pack.baseZ = oldestSector.z;
					pack.revision = revision;
					getClient().getNetwork().send(pack);
					sX.lastUsed = System.currentTimeMillis();
				}
			}
		}
	}

	public final void flagSectorAsBlank(final SectorLocation p) {
		SectorStorage ss = map.get(p);
		if (ss == null) {
			ss = new SectorStorage();
			ss.empty = true;
			map.put(p, ss);
		} else {
			ss.empty = true;
		}
	}

	@Override
	public SectorStorage getSectorStorage(int x, int y, int z) {
		synchronized (getMutex()) {
			SectorLocation p = new SectorLocation(x, y, z);
			SectorStorage sS = map.get(p);
			if (sS == null || (sS.data == null && !sS.empty)) {
				if (!loadQueue.contains(p)) { // TODO FASTER
					loadQueue.addLast(p);
					getMutex().notify();
				}
				return null;
			}
			return sS;
		}
	}

	@Override
	public Map<SectorLocation, SectorStorage> loadedMap() {
		return Collections.unmodifiableMap(map);
	}
}
