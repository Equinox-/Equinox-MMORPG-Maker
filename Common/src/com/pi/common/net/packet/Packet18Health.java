package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.game.LivingEntity;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet representing a health update for an entity.
 * 
 * @author Westin
 * 
 */
public class Packet18Health extends Packet {
	/**
	 * The entity this packet updates.
	 */
	public int entityID;
	/**
	 * The new health value.
	 */
	public float health;

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entityID);
		pOut.writeFloat(health);
	}

	@Override
	public final int getLength() {
		return SizeOf.INT + SizeOf.FLOAT;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		entityID = pIn.readInt();
		health = pIn.readFloat();
	}

	/**
	 * Creates a packet health packet for the given entity.
	 * 
	 * @param lE the entity
	 * @return the created packet
	 */
	public static Packet18Health create(final LivingEntity lE) {
		Packet18Health p = new Packet18Health();
		p.entityID = lE.getEntityID();
		p.health = lE.getHealth();
		return p;
	}

}
