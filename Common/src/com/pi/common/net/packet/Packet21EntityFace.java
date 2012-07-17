package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.Direction;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet representing an entity changing it's face direction.
 * 
 * @author Westin
 * 
 */
public class Packet21EntityFace extends Packet {
	/**
	 * The entity that is changing face.
	 */
	public int entityID;
	/**
	 * The new face.
	 */
	public Direction face;

	/**
	 * Creates an entity attack packet with the given entity ID number.
	 * 
	 * @param entity the attacking entity
	 * @param face the new direction
	 * @return the packet instance
	 */
	public static Packet21EntityFace create(final int entity,
			final Direction face) {
		Packet21EntityFace p = new Packet21EntityFace();
		p.entityID = entity;
		p.face = face;
		return p;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entityID);
		if (face == null) {
			pOut.writeByte(0);
		} else {
			pOut.writeByte(face.ordinal());
		}
	}

	@Override
	public final int getLength() {
		return 0;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		entityID = pIn.readInt();
		byte iFace = pIn.readByte();
		if (iFace >= 0 && iFace < Direction.values().length) {
			face = Direction.values()[iFace];
		} else {
			face = Direction.UP;
		}
	}
}
