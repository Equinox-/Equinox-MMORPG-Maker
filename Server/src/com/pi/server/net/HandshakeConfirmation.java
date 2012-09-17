package com.pi.server.net;

import com.pi.common.constants.NetworkConstants;
import com.pi.common.net.packet.Packet;

/**
 * A simple container class representing a queued handshake confirmation.
 * 
 * @author Westin
 * 
 */
public class HandshakeConfirmation {
	/**
	 * The time the packet was sent.
	 */
	private long timestamp;
	/**
	 * The packet that was sent.
	 */
	private Packet packet;

	/**
	 * Creates a handshake confirmation object for the given packet, setting the
	 * sent time to the current time.
	 * 
	 * @param sPacket the packet
	 */
	public HandshakeConfirmation(final Packet sPacket) {
		this.packet = sPacket;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Gets the packet that this confirmation object represents.
	 * 
	 * @return the packet
	 */
	public final Packet getPacket() {
		return packet;
	}

	/**
	 * Checks if the packet represented by this object needs to be re-sent.
	 * 
	 * The equation this uses is
	 * <code>{@link System#currentTimeMillis()} >= {@link HandshakeConfirmation#timestamp} + {@link NetworkConstants#HANDSHAKE_EXPIRY_TIME}</code>
	 * 
	 * @return if this packet must be re-sent
	 */
	public final boolean needToResend() {
		return System.currentTimeMillis() >= timestamp
				+ NetworkConstants.HANDSHAKE_EXPIRY_TIME;
	}
}
