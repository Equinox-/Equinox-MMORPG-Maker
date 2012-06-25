package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.database.Location;
import com.pi.common.game.Entity;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent from the server to the client to perform a short entity
 * movement.
 * 
 * @author Westin
 * 
 */
public class Packet16EntityMove extends EntityMovementPacket {
	public int entity;

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entity);
		super.writeData(pOut);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		entity = pIn.readInt();
		super.readData(pIn);
	}

	/**
	 * Creates an instance of this packet for the given entity.
	 * 
	 * @param ent the entity to create this packet with
	 * @return the packet instance
	 */
	public static Packet16EntityMove create(final Entity ent) {
		Packet16EntityMove pack = new Packet16EntityMove();
		pack.entity = ent.getEntityID();
		pack.setCoordinateParts(ent.getGlobalX(),
				ent.getGlobalZ());
		return pack;
	}

	/**
	 * Creates an instance of this packet for the given entity.
	 * 
	 * @param entityID the entity id
	 * @param loc the location
	 * @return the packet instance
	 */
	public static Packet16EntityMove create(final int entityID,
			final Location loc) {
		Packet16EntityMove pack = new Packet16EntityMove();
		pack.entity = entityID;
		pack.setCoordinateParts(loc.getGlobalX(),
				loc.getGlobalZ());
		return pack;
	}

	@Override
	public final int getLength() {
		return SizeOf.INT + super.getLength();
	}
}
