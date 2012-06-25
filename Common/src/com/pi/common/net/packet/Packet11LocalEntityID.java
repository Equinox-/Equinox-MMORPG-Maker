package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent from the server to the client to set the client's entity
 * identification number.
 * 
 * @author Westin
 * 
 */
public class Packet11LocalEntityID extends Packet {
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
	 * Create an instance of this packet with the given entity id.
	 * 
	 * @param entity the client's entity id
	 * @return the packet instance
	 */
	public static Packet11LocalEntityID create(final int entity) {
		Packet11LocalEntityID pack = new Packet11LocalEntityID();
		pack.entityID = entity;
		return pack;
	}

	@Override
	public final int getLength() {
		return SizeOf.INT;
	}
}
