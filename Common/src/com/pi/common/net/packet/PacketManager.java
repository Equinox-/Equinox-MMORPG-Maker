package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.debug.PILogger;
import com.pi.common.net.PacketInputStream;
import com.pi.common.util.ClassIntPairManager;

/**
 * Class managing packet registration and packet identification.
 * 
 * @author Westin
 * 
 */
public final class PacketManager {
	/**
	 * The pair manager that is currently in use.
	 */
	private static final ClassIntPairManager<Packet> INSTANCE =
			new ClassIntPairManager<Packet>();

	/**
	 * Gets the current pair manager INSTANCE being used by this packet manager.
	 * 
	 * @return the pair manager
	 */
	public static ClassIntPairManager<Packet> getInstance() {
		return INSTANCE;
	}

	static {
		INSTANCE.registerPair(Packet0Handshake.class);
		INSTANCE.registerPair(Packet1Login.class);
		INSTANCE.registerPair(Packet2Alert.class);
		INSTANCE.registerPair(Packet3Register.class);
		INSTANCE.registerPair(Packet4Sector.class);
		INSTANCE.registerPair(Packet5SectorRequest.class);
		INSTANCE.registerPair(Packet6BlankSector.class);
		INSTANCE.registerPair(Packet7EntityTeleport.class);
		INSTANCE.registerPair(Packet8EntityDispose.class);
		INSTANCE.registerPair(Packet9EntityData.class);
		INSTANCE.registerPair(Packet10EntityDataRequest.class);
		INSTANCE.registerPair(Packet11LocalEntityID.class);
		INSTANCE.registerPair(Packet12EntityDefRequest.class);
		INSTANCE.registerPair(Packet13EntityDef.class);
		INSTANCE.registerPair(Packet14ClientMove.class);
		INSTANCE.registerPair(Packet15GameState.class);
		INSTANCE.registerPair(Packet16EntityMove.class);
		INSTANCE.registerPair(Packet17Clock.class);
		INSTANCE.registerPair(Packet18EntityComponent.class);
		INSTANCE.registerPair(Packet19Interact.class);
		INSTANCE.registerPair(Packet20EntityAttack.class);
		INSTANCE.registerPair(Packet21EntityFace.class);
		INSTANCE.registerPair(Packet22ItemDefRequest.class);
		INSTANCE.registerPair(Packet23ItemDef.class);
		INSTANCE.registerPair(Packet24InventoryData.class);
		INSTANCE.registerPair(Packet25InventoryUpdate.class);
		INSTANCE.trimMaps();
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

			Class<? extends Packet> clazz =
					INSTANCE.getPairClass(id);
			if (clazz != null) {
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
