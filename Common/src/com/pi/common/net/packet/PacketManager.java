package com.pi.common.net.packet;

import java.io.IOException;
import java.util.Arrays;

import com.pi.common.debug.PILogger;
import com.pi.common.net.PacketInputStream;

/**
 * Class managing packet registration and packet identification.
 * 
 * @author Westin
 * 
 */
public final class PacketManager {
	/**
	 * The packet class mapping array of this packet manager.
	 */
	@SuppressWarnings("unchecked")
	private static Class<? extends Packet>[] idMapping =
			new Class[0];

	static {
		try {
			registerPacket(Packet0Disconnect.class);
			registerPacket(Packet1Login.class);
			registerPacket(Packet2Alert.class);
			registerPacket(Packet3Register.class);
			registerPacket(Packet4Sector.class);
			registerPacket(Packet5SectorRequest.class);
			registerPacket(Packet6BlankSector.class);
			registerPacket(Packet7EntityTeleport.class);
			registerPacket(Packet8EntityDispose.class);
			registerPacket(Packet9EntityData.class);
			registerPacket(Packet10EntityDataRequest.class);
			registerPacket(Packet11LocalEntityID.class);
			registerPacket(Packet12EntityDefRequest.class);
			registerPacket(Packet13EntityDef.class);
			registerPacket(Packet14ClientMove.class);
			registerPacket(Packet15GameState.class);
			registerPacket(Packet16EntityMove.class);
			registerPacket(Packet17Clock.class);
			registerPacket(Packet18Health.class);
			registerPacket(Packet19Attack.class);
			registerPacket(Packet22ItemDefRequest.class);
			registerPacket(Packet23ItemDef.class);
			registerPacket(Packet24InventoryData.class);
			registerPacket(Packet25InventoryUpdate.class);
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	/**
	 * Registers a packet with this packet manager.
	 * 
	 * @param pClass the packet class
	 * @throws InstantiationException if the identification number wasn't
	 *             fetched through the {@link Packet#getID()} method.
	 * @throws IllegalAccessException if the identification number wasn't
	 *             fetched through the {@link Packet#getID()} method.
	 */
	public static void registerPacket(
			final Class<? extends Packet> pClass)
			throws IllegalAccessException,
			InstantiationException {
		int id = pClass.newInstance().getID();
		if (id < idMapping.length && idMapping[id] != null) {
			throw new IllegalStateException(
					"Duplicate packet id: " + id);
		} else {
			if (id >= idMapping.length) {
				idMapping =
						Arrays.copyOf(idMapping.clone(), Math
								.max(id + 1, idMapping.length));
			}
			idMapping[id] = pClass;
		}
	}

	/**
	 * Creates an instance of the packet with the specified identification
	 * number, and returns it.
	 * 
	 * @param log the logger for errors
	 * @param id the packet id to create an instance of
	 * @return the packet instance, or <code>null</code> if the identification
	 *         number is bad, or there was a problem invoking the method.
	 */
	public static Packet getPacket(final PILogger log,
			final int id) {
		try {
			if (id >= 0 && id < idMapping.length) {
				Class<? extends Packet> clazz = idMapping[id];
				return clazz.newInstance();
			} else {
				log.severe("Bad packet ID: " + id);
				return null;
			}
		} catch (Exception e) {
			log.printStackTrace(e);
			log.severe("Skipping packet by id: " + id);
			return null;
		}
	}

	/**
	 * Gets the packet from a packet input stream. This method doesn't read the
	 * actual packet data though. Invoke the
	 * {@link Packet#readData(PacketInputStream)} method later, to finish
	 * reading the data.
	 * 
	 * @see PacketManager#getPacket(PILogger, int)
	 * @param log the logger for errors
	 * @param pIn the input stream
	 * @return the packet instance
	 * @throws IOException if the packet identification number was invalid
	 */
	public static Packet getPacket(final PILogger log,
			final PacketInputStream pIn) throws IOException {
		int id = pIn.readByte();
		Packet packet = getPacket(log, id);
		if (packet == null) {
			throw new IOException("Bad packet id: " + id);
		}
		// packet.timeStamp = pIn.readLong();
		return packet;
	}

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private PacketManager() {
	}
}
