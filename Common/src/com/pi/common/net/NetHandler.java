package com.pi.common.net;

import java.lang.reflect.Method;

import com.pi.common.debug.PILogger;
import com.pi.common.net.packet.Packet;

/**
 * A class for processing abstract packets.
 * 
 * @author Westin
 * 
 */
public abstract class NetHandler {
	/**
	 * Generic method to process packets without a custom method. This should
	 * not be used, except for an error message.
	 * 
	 * @see NetHandler#processPacket(Packet)
	 * @param p the packet to process
	 */
	protected abstract void process(Packet p);

	/**
	 * The logger used to log messages on this net handler.
	 * 
	 * @return the logger
	 */
	protected abstract PILogger getLog();

	/**
	 * Sends a handshake confirming that the given packet was received.
	 * 
	 * @param packetID the packet id
	 */
	protected void sendHandshake(final int packetID) {
	}

	/**
	 * Processes the provided packet, first looking for a method by the name of
	 * 'process' and an argument type of the same sub-packet class as the
	 * provided packet, and invoking it. If this fails, it falls back on the
	 * generic {@link NetHandler#process(Packet)} method.
	 * 
	 * @param p the packet to process
	 */
	public final void processPacket(final Packet p) {
		try {
			Method m =
					getClass()
							.getMethod("process", p.getClass());
			m.invoke(this, p);
			if (p.requiresHandshake()) {
				sendHandshake(p.getID());
			}
			return;
		} catch (Exception e) {
			getLog().severe(
					getClass().getSimpleName()
							+ ": No custom method for packet: "
							+ p.getName());
		}
		process(p);
		if (p.requiresHandshake()) {
			sendHandshake(p.getID());
		}
	}
}
