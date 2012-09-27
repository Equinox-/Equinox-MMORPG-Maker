package com.pi.common.net;

import java.lang.reflect.Method;

import com.pi.common.debug.PILogger;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.PacketManager;
import com.pi.common.util.ObjectHeap;

/**
 * A class for processing abstract packets.
 * 
 * @author Westin
 * 
 */
public abstract class NetHandler {
	/**
	 * Method lookup map to speed up packet processing times.
	 */
	private final ObjectHeap<Method> methodLookupMap =
			new ObjectHeap<Method>(PacketManager.getInstance()
					.getPairCount());

	/**
	 * Generates the method lookup map.
	 */
	public NetHandler() {
		Class<? extends NetHandler> clazz = getClass();
		for (int id = 0; id < PacketManager.getInstance()
				.getPairCount(); id++) {
			Class<? extends Packet> packetClass =
					PacketManager.getInstance().getPairClass(id);
			if (packetClass != null) {
				try {
					Method m =
							clazz.getMethod("process",
									packetClass);
					if (m != null) {
						methodLookupMap.set(id, m);
					}
				} catch (NoSuchMethodException e) {
					// This packet handler has no custom method for a packet. It
					// probably never gets it.
				}
			}
		}
	}

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
		Method m = methodLookupMap.get(p.getID());
		if (m != null) {
			try {
				m.invoke(this, p);
			} catch (Exception e) {
				getLog().printStackTrace(e);
			}
		} else {
			getLog().severe(
					getClass().getSimpleName()
							+ ": No custom method for packet: "
							+ p.getName());
			process(p);
		}
		if (p.requiresHandshake()) {
			sendHandshake(p.getID());
		}
	}
}
