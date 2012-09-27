package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketOutputStream;

/**
 * Get network packet class that represents any packet sent or received through
 * the network.
 * <p>
 * If the {@link Packet#getID()} method is not overridden in the sub class, then
 * the class name must have a single solid block of numbers that represent the
 * packet's id number. In addition, all packets must be registered with the
 * packet manager, using the {@link PacketManager#registerPacket(Class)} method.
 * 
 * @author Westin
 * 
 */
public abstract class Packet implements PacketObject,
		Comparable<Packet> {
	/**
	 * The time that this packet was created.
	 */
	private long timeStamp = System.currentTimeMillis();

	/**
	 * Gets the name of this packet, this class' name if not overridden.
	 * 
	 * @return the packet name
	 */
	public final String getName() {
		return getClass().getSimpleName();
	}

	/**
	 * Writes this packet to the provided packet output stream.
	 * 
	 * @param pOut the stream to write to
	 * @throws IOException if an error occurs
	 */
	public final void writePacket(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeByte(getID());
		// pOut.writeLong(timeStamp);
		writeData(pOut);
	}

	/**
	 * Gets the byte length of this packet.
	 * 
	 * @return the byte length
	 */
	public final int getPacketLength() {
		return 1 + getLength();
	}

	/**
	 * Gets the identification number for this packet from the class name.
	 * 
	 * @return the packet id
	 */
	public final int getID() {
		return PacketManager.getInstance().getPairID(getClass());
	}

	@Override
	public final int compareTo(final Packet p) {
		if (p == this || p.timeStamp == timeStamp) {
			return 0;
		}
		if (timeStamp < p.timeStamp) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * Sets this packet's time stamp.
	 * 
	 * @param l the new time stamp
	 */
	public final void setTimeStamp(final long l) {
		this.timeStamp = l;
	}

	/**
	 * Gets this packet's time stamp.
	 * 
	 * @return the timeStamp
	 */
	public final long getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Checks if this packet requires a handshake to be send to the server to
	 * confirm it's reception.
	 * 
	 * @return <code>true</code> if this packet requires a handshake,
	 *         <code>false</code> if not
	 */
	public boolean requiresHandshake() {
		return false;
	}
}
