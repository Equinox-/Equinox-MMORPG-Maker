package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent by the server to the client to destroy an entity on the client.
 * 
 * @author Westin
 * 
 */
public class Packet8EntityDispose extends Packet {
	public int entityID;

	/**
	 * Create an entity disposal packet for the given entity identification
	 * number.
	 * 
	 * @param id the entity id number
	 * @return the packet instance
	 */
	public static Packet8EntityDispose create(final int id) {
		Packet8EntityDispose p = new Packet8EntityDispose();
		p.entityID = id;
		return p;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entityID);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		entityID = pIn.readInt();
	}

	@Override
	public final int getLength() {
		return SizeOf.INT;
	}
}
