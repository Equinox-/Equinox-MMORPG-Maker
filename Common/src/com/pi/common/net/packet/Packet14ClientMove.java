package com.pi.common.net.packet;

import com.pi.common.database.Location;

/**
 * This packet is used by the client to transmit movement to the server. This
 * packet very similar to the {@link Packet16EntityMove} packet, except it
 * doesn't use an entity ID.
 * 
 * @author Westin
 * 
 */
public class Packet14ClientMove extends EntityMovementPacket {
	/**
	 * Creates an instance of this packet for the specified location.
	 * 
	 * @param l the location to pack
	 * @return the packet instance
	 */
	public static Packet14ClientMove create(final Location l) {
		Packet14ClientMove pack = new Packet14ClientMove();
		pack.setCoordinateParts(l.getGlobalX(), l.getGlobalZ());
		return pack;
	}

	@Override
	public final int getLength() {
		return super.getLength();
	}
}
