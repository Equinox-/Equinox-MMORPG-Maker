package com.pi.client.world;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi.client.Client;
import com.pi.client.ClientThread;
import com.pi.client.database.Paths;
import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;
import com.pi.common.database.io.DatabaseIO;
import com.pi.common.net.packet.Packet5SectorRequest;

/**
 * The sector manager, managing loading, saving, and requesting of sectors from
 * the server.
 * 
 * @author Westin
 * 
 */
public class SectorManager extends ClientThread implements
		com.pi.common.world.SectorManager {
	/**
	 * The amount of time in milliseconds to purge a sector from the memory.
	 */
	public static final int SECTOR_EXPIRY = 60000;
	/**
	 * The amount of time in milliseconds to re-request a sector from the
	 * server.
	 */
	public static final int SECTOR_REQUEST_EXPIRY = 60000;

	/**
	 * The sector load queue.
	 */
	private Queue<SectorLocation> loadQueue =
			new LinkedBlockingQueue<SectorLocation>();
	/**
	 * The storage map for the sectors.
	 */
	private Hashtable<SectorLocation, SectorStorage> map =
			new Hashtable<SectorLocation, SectorStorage>();
	/**
	 * The sent request storage map for the sectors.
	 */
	private Hashtable<SectorLocation, Long> sentRequests =
			new Hashtable<SectorLocation, Long>();

	/**
	 * Create a sector manager for the provided client.
	 * 
	 * @param client the client to bind this manager to
	 */
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

	/**
	 * Sets the sector data in the storage map, and also save the sector to the
	 * cache.
	 * 
	 * @param sector the sector to save
	 */
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
					getClient().getLog().printStackTrace(e);
				}
			} else {
				doRequest();
				removeExpired();
			}
		}
	}

	/**
	 * Remove the expired sectors from the RAM.
	 */
	private void removeExpired() {
		for (SectorLocation i : map.keySet()) {
			if (System.currentTimeMillis() - map.get(i).lastUsed >= SECTOR_EXPIRY) {
				map.remove(i);
			}
		}
	}

	/**
	 * Do the requests in the load queue.
	 */
	private void doRequest() {
		SectorLocation oldestSector = loadQueue.poll();
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
								+ SECTOR_REQUEST_EXPIRY < System
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

	/**
	 * Flag the sector at the given location as blank, and no longer request it
	 * from the server.
	 * 
	 * @param p the sector location to flag blank
	 */
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
	public final SectorStorage getSectorStorage(final int x,
			final int y, final int z) {
		synchronized (getMutex()) {
			SectorLocation p = new SectorLocation(x, y, z);
			SectorStorage sS = map.get(p);
			if (sS == null || (sS.data == null && !sS.empty)) {
				if (!loadQueue.contains(p)) { // TODO FASTER
					loadQueue.add(p);
					getMutex().notify();
				}
				return null;
			}
			return sS;
		}
	}

	@Override
	public final Map<SectorLocation, SectorStorage> loadedMap() {
		return Collections.unmodifiableMap(map);
	}
}
