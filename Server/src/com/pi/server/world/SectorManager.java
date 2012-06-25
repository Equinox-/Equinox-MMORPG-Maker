package com.pi.server.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

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

public class SectorManager extends ServerThread implements
		com.pi.common.world.SectorManager {
	public static final int sectorExpiry = 300000; // 5 Minutes

	private LinkedList<SectorLocation> loadQueue =
			new LinkedList<SectorLocation>();
	private Hashtable<SectorLocation, SectorStorage> map =
			new Hashtable<SectorLocation, SectorStorage>();

	/**
	 * Create the sector manager for the given server.
	 * 
	 * @param server the server
	 */
	public SectorManager(final Server server) {
		super(server);
		this.mutex = new Object();
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
		synchronized (mutex) {
			server.getLog().info("Request sector");
			SectorStorage sec =
					getSectorStorage(req.baseX, req.baseY,
							req.baseZ);
			if (sec != null
					&& (sec.getSectorRaw() != null || sec
							.isEmpty())) {
				if (sec.isEmpty()) {
					Packet6BlankSector packet =
							new Packet6BlankSector();
					packet.baseX = req.baseX;
					packet.baseY = req.baseY;
					packet.baseZ = req.baseZ;
					server.getClientManager()
							.getClient(clientID).getNetClient()
							.send(packet);
				} else {
					Sector sector = sec.getSector();
					server.getLog().info(
							sector.getRevision() + ":"
									+ req.revision);
					if (sector.getRevision() != req.revision) {

						Packet4Sector packet =
								new Packet4Sector();
						packet.sector = sector;

						Client cli =
								server.getClientManager()
										.getClient(clientID);
						// cli.getNetClient().sendRaw(sec.pack); TODO
						cli.getNetClient().send(packet);
					}
				}
			}
		}
	}

	@Override
	public final Sector getSector(final int x, final int y,
			final int z) {
		SectorStorage ss = getSectorStorage(x, y, z);
		return ss != null ? ss.getSector() : null;
	}

	@Override
	public final boolean isEmptySector(final int x, final int y,
			final int z) {
		SectorStorage ss = getSectorStorage(x, y, z);
		return ss != null ? ss.isEmpty() : false;
	}

	@Override
	public final SectorStorage getSectorStorage(final int x,
			final int y, final int z) {
		synchronized (mutex) {
			SectorLocation p = new SectorLocation(x, y, z);
			SectorStorage sS = map.get(p);
			if (sS == null
					|| (sS.getSectorRaw() == null && !sS
							.isEmpty())) {
				if (!loadQueue.contains(p)) {// TODO FASTER
					loadQueue.addLast(p);
					mutex.notify();
				}
				return null;
			}
			return sS;
		}
	}

	public final void setSector(final Sector sector) {
		synchronized (mutex) {
			ServerSectorStorage sec =
					(ServerSectorStorage) map.get(sector
							.getSectorLocation());
			if (sec == null) {
				sec = new ServerSectorStorage();
			}
			sec.lastUsed = System.currentTimeMillis();
			sec.data = sector;
			sec.updatePacketData();
			map.put(sector.getSectorLocation(), sec);
		}

		// Write Sector
		try {
			DatabaseIO.write(Paths.getSectorFile(
					sector.getSectorX(), sector.getPlane(),
					sector.getSectorZ()), sector);
		} catch (IOException e) {
			server.getLog().printStackTrace(e);
		}
	}

	@Override
	public final void loop() {
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
			if (System.currentTimeMillis()
					- map.get(i).getLastUsedTime() > sectorExpiry) {
				map.remove(i);
				server.getLog().fine(
						"Dropped sector: " + i.toString());
			}
		}
	}

	private void doRequest() {
		SectorLocation oldestSector = loadQueue.removeFirst();
		ServerSectorStorage sX =
				(ServerSectorStorage) map.get(oldestSector);
		if (sX == null || (sX.data == null && !sX.empty)) {
			if (sX == null) {
				sX = new ServerSectorStorage();
			}
			try {
				sX.data =
						(Sector) DatabaseIO.read(Paths
								.getSectorFile(oldestSector),
								Sector.class);
				sX.updatePacketData();
				sX.empty = false;
				sX.lastUsed = System.currentTimeMillis();
				server.getLog().finer(
						"Loaded sector "
								+ oldestSector.toString());
				map.put(oldestSector, sX);
			} catch (FileNotFoundException e) {
				sX.data = null;
				sX.empty = true;
				sX.lastUsed = System.currentTimeMillis();
				map.put(oldestSector, sX);
				server.getLog().finest(
						"Flagged as empty: "
								+ oldestSector.toString());
			} catch (IOException e) {
				server.getLog().printStackTrace(e);
			}
		}
	}

	public static class ServerSectorStorage extends
			SectorStorage {
		public long lastUsed;
		public Sector data;
		public byte[] pack;
		public boolean empty = false;

		public final void updatePacketData() {
			Packet4Sector p = new Packet4Sector();
			p.sector = data;
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

	public static class ClientSectorRequest {
		public int clientId;
		public int revision;
		public int baseX, baseY, baseZ;

		public ClientSectorRequest(final int client,
				final Packet5SectorRequest req) {
			this.clientId = client;
			this.revision = req.revision;
			this.baseX = req.baseX;
			this.baseY = req.baseY;
			this.baseZ = req.baseZ;
		}

		@Override
		public final boolean equals(final Object o) {
			if (o instanceof ClientSectorRequest) {
				ClientSectorRequest req =
						(ClientSectorRequest) o;
				return this.clientId == req.clientId
						&& this.baseX == req.baseX
						&& this.baseY == req.baseY
						&& this.baseZ == req.baseZ;
			}
			return false;
		}
	}

	@Override
	public final Map<SectorLocation, SectorStorage> loadedMap() {
		synchronized (mutex) {
			return Collections.unmodifiableMap(map);
		}
	}
}
