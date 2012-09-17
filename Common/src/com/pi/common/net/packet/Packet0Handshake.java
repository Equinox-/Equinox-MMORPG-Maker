package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet that the client sends to the server to confirm that it received a
 * packet. with a given reason and details.
 * 
 * @author Westin
 * 
 */
public class Packet0Handshake extends Packet {

	/**
	 * The packet ID this packet confirms reception of.
	 */
	public int packetShake;

	/**
	 * Creates an instance of the handshake packet for the given packet ID.
	 * 
	 * @param packetHandshake the packet id that this client is confirming
	 * @return the packet instance
	 */
	public static Packet0Handshake create(
			final int packetHandshake) {
		Packet0Handshake pack = new Packet0Handshake();
		pack.packetShake = packetHandshake;
		return pack;
	}

	@Override
	public final void writeData(final PacketOutputStream dOut)
			throws IOException {
		dOut.writeInt(packetShake);
	}

	@Override
	public final void readData(final PacketInputStream dIn)
			throws IOException {
		packetShake = dIn.readInt();
	}

	@Override
	public final int getLength() {
		return SizeOf.INT;
	}
}
