package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.database.Location;
import com.pi.common.database.TileLayer;
import com.pi.common.game.Entity;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent by the server to the client to signify an entity teleport
 * event.
 * 
 * @author Westin
 * 
 */
public class Packet7EntityTeleport extends Packet {
	public int entityID;
	public Location moved;
	public TileLayer entityLayer;

	/**
	 * Create a teleport packet for the given entity.
	 * 
	 * @param ent the entity to create a packet for
	 * @return the packet instance
	 */
	public static Packet7EntityTeleport create(final Entity ent) {
		Packet7EntityTeleport p = new Packet7EntityTeleport();
		p.entityID = ent.getEntityID();
		p.moved =
				new Location(ent.getGlobalX(), ent.getPlane(),
						ent.getGlobalZ());
		p.entityLayer = ent.getLayer();
		return p;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		if (moved == null) {
			moved = new Location();
		}
		pOut.writeInt(entityID);
		moved.writeData(pOut);
		pOut.writeInt(entityLayer.ordinal());
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		entityID = pIn.readInt();
		if (moved == null) {
			moved = new Location();
		}
		moved.readData(pIn);
		int eLI = pIn.readInt();
		if (eLI >= 0 && eLI < TileLayer.MAX_VALUE.ordinal()) {
			entityLayer = TileLayer.values()[eLI];
		} else {
			entityLayer = TileLayer.MASK1;
		}
	}

	@Override
	public final int getLength() {
		if (moved == null) {
			moved = new Location();
		}
		return (SizeOf.INT * 2) + moved.getLength();
	}
}
