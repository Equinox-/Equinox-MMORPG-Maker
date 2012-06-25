package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent by the client to request entity data from the server.
 * 
 * @author Westin
 * 
 */
public class Packet10EntityDataRequest extends Packet {
	public int entityID;

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

	/**
	 * Creates a entity data request with the given entity id.
	 * 
	 * @param entity the entity ID
	 * @return the packet instance
	 */
	public static Packet10EntityDataRequest create(
			final int entity) {
		Packet10EntityDataRequest r =
				new Packet10EntityDataRequest();
		r.entityID = entity;
		return r;
	}

	@Override
	public final int getLength() {
		return SizeOf.INT;
	}
}
