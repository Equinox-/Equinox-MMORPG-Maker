package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet representing an entity attacking something.
 * 
 * @author Westin
 * 
 */
public class Packet20EntityAttack extends Packet {
	/**
	 * The entity that is attacking.
	 */
	public int entityID;

	/**
	 * Creates an entity attack packet with the given entity ID number.
	 * 
	 * @param entity the attacking entity
	 * @return the packet instance
	 */
	public static Packet20EntityAttack create(final int entity) {
		Packet20EntityAttack p = new Packet20EntityAttack();
		p.entityID = entity;
		return p;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entityID);
	}

	@Override
	public final int getLength() {
		return 0;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		entityID = pIn.readInt();
	}
}
