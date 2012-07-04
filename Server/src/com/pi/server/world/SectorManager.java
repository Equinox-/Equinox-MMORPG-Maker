package com.pi.server.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;
import com.pi.common.database.io.DatabaseIO;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.Packet4Sector;
import com.pi.common.net.packet.Packet5SectorRequest;
import com.pi.common.net.packet.Packet6BlankSector;
import com.pi.server.Server;
import com.pi.server.ServerThread;
import com.pi.server.client.Client;
import com.pi.server.database.Paths;

/**
 * The sector manager, for loading, saving, and processing client sector
 * requests.
 * 
 * @author Westin
 * 
 */
public class SectorManager extends ServerThread implements
		com.pi.common.world.SectorManager {
	/**
	 * The amount of time in milliseconds to purge a sector from the memory.
	 */
	public static final int SECTOR_EXPIRY = 300000;

	/**
	 * The sector load queue.
	 */
	private Queue<SectorLocation> loadQueue =
			new LinkedBlockingQueue<SectorLocation>();

	/**
	 * The storage map.
	 */
	private Hashtable<SectorLocation, SectorStorage> map =
			new Hashtable<SectorLocation, SectorStorage>();

	/**
	 * Create the sector manager for the given server.
	 * 
	 * @param server the server
	 */
	public SectorManager(final Server server) {
		super(server);
		createMutex();
		start();
	}

	/**
	 * Requests a sector for the provided client id, and sector request.
	 * 
	 * @param clientID the client requesting the sector
	 * @param req the request packet
	 */
	public final void requestSector(final int clientID,
			final Packet5SectorRequest req) {
		synchronized (getMutex()) {
			getServer().getLog().info("Request sector");
			ServerSectorStorage sec =
					(ServerSectorStorage) getSectorStorage(
							req.baseX, req.baseY, req.baseZ);
			if (sec != null
					&& (sec.getSectorRaw() != null || sec
							.isEmpty())) {
				if (sec.isEmpty()) {
					Packet6BlankSector packet =
							new Packet6BlankSector();
					packet.baseX = req.baseX;
					packet.baseY = req.baseY;
					packet.baseZ = req.baseZ;
					getServer().getClientManager()
							.getClient(clientID).getNetClient()
							.send(packet);
				} else {
					Sector sector = sec.getSector();
					getServer().getLog().info(
							sector.getRevision() + ":"
									+ req.revision);
					if (sector.getRevision() != req.revision) {

						Packet4Sector packet =
								new Packet4Sector();
						packet.sector = sector;

						Client cli =
								getServer().getClientManager()
										.getClient(clientID);
						cli.getNetClient().sendRaw(sec.pack);
						// cli.getNetClient().send(packet);
					}
				}
			}
		}
	}

	@Override
	public final Sector getSector(final int x, final int y,
			final int z) {
		SectorStorage ss = getSectorStorage(x, y, z);
		if (ss != null) {
			return ss.getSector();
		} else {
			return null;
		}
	}

	@Override
	public final boolean isEmptySector(final int x, final int y,
			final int z) {
		SectorStorage ss = getSectorStorage(x, y, z);
		if (ss != null) {
			return ss.isEmpty();
		} else {
			return false;
		}
	}

	@Override
	public final SectorStorage getSectorStorage(final int x,
			final int y, final int z) {
		synchronized (getMutex()) {
			SectorLocation p = new SectorLocation(x, y, z);
			SectorStorage sS = map.get(p);
			if (sS == null
					|| (sS.getSectorRaw() == null && !sS
							.isEmpty())) {
				loadQueue.add(p);
				getMutex().notify();
				return null;
			}
			return sS;
		}
	}

	/**
	 * Saves this sector to the disk, and updates it in the mapping.
	 * 
	 * @param sector the sector data
	 */
	public final void setSector(final Sector sector) {
		synchronized (getMutex()) {
			ServerSectorStorage sec =
					(ServerSectorStorage) map.get(sector
							.getSectorLocation());
			if (sec == null) {
				sec = new ServerSectorStorage();
			}
			sec.updateLastTimeUsed();
			sec.setSector(sector);
			sec.updatePacketData();
			map.put(sector.getSectorLocation(), sec);
		}

		// Write Sector
		try {
			DatabaseIO.write(Paths.getSectorFile(
					sector.getSectorX(), sector.getPlane(),
					sector.getSectorZ()), sector);
		} catch (IOException e) {
			getServer().getLog().printStackTrace(e);
		}
	}

	@Override
	public final void loop() {
		synchronized (getMutex()) {
			if (loadQueue.size() <= 0) {
				try {
					getMutex().wait();
				} catch (InterruptedException e) {
					getServer()
							.getLog()
							.severe("InterruptedException in the sector manager!");
				}
			} else {
				// Do a request
				SectorLocation oldestSector = loadQueue.poll();
				ServerSectorStorage sX =
						(ServerSectorStorage) map
								.get(oldestSector);
				if (sX == null
						|| (sX.getSectorRaw() == null && !sX
								.isEmpty())) {
					if (sX == null) {
						sX = new ServerSectorStorage();
					}
					try {
						sX.setSector((Sector) DatabaseIO.read(
								Paths.getSectorFile(oldestSector),
								new Sector()));
						sX.updatePacketData();
						sX.setEmpty(false);
						sX.updateLastTimeUsed();
						getServer().getLog().finer(
								"Loaded sector "
										+ oldestSector
												.toString());
						map.put(oldestSector, sX);
					} catch (FileNotFoundException e) {
						sX.setSector(null);
						sX.setEmpty(true);
						sX.updateLastTimeUsed();
						map.put(oldestSector, sX);
						getServer().getLog().finest(
								"Flagged as empty: "
										+ oldestSector
												.toString());
					} catch (IOException e) {
						getServer().getLog().printStackTrace(e);
					}
				}

				// Remove the expired sectors
				for (SectorLocation i : map.keySet()) {
					if (System.currentTimeMillis()
							- map.get(i).getLastUsedTime() > SECTOR_EXPIRY) {
						map.remove(i);
						getServer().getLog().fine(
								"Dropped sector: "
										+ i.toString());
					}
				}
			}
		}
	}

	/**
	 * Container class for storing sector load state and the raw packet data.
	 * 
	 * @author Westin
	 * 
	 */
	public static class ServerSectorStorage extends
			SectorStorage {
		/**
		 * The raw packet data.
		 */
		private byte[] pack;

		/**
		 * Updates the raw packet data for this sector.
		 */
		private void updatePacketData() {
			Packet4Sector p = new Packet4Sector();
			p.sector = super.getSector();
			try {
				PacketOutputStream pO =
						new PacketOutputStream(
								ByteBuffer.allocate(p
										.getPacketLength()));
				p.writePacket(pO);
				pack = pO.getByteBuffer().array();
			} catch (Exception e) {
				pack = null;
			}
		}
	}

	@Override
	public final Map<SectorLocation, SectorStorage> loadedMap() {
		synchronized (getMutex()) {
			return Collections.unmodifiableMap(map);
		}
	}
}
