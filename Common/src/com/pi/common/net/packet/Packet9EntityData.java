package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.database.Location;
import com.pi.common.database.TileLayer;
import com.pi.common.game.Entity;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent by the server to the client to initially create an entity on
 * the client.
 * 
 * @author Westin
 * 
 */
public class Packet9EntityData extends Packet {
	public Location loc;
	public TileLayer layer;
	public int defID;
	public int entID;

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entID);
		if (loc == null) {
			loc = new Location();
		}
		loc.writeData(pOut);
		pOut.writeInt(layer.ordinal());
		pOut.writeInt(defID);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		entID = pIn.readInt();
		if (loc == null) {
			loc = new Location();
		}
		loc.readData(pIn);
		int lI = pIn.readInt();
		if (lI >= 0 && lI < TileLayer.MAX_VALUE.ordinal()) {
			layer = TileLayer.values()[lI];
		} else {
			layer = TileLayer.MASK1;
		}
		defID = pIn.readInt();
	}

	/**
	 * Create an instance of the entity data packet for the given entity.
	 * 
	 * @param e the entity to create a packet for
	 * @return the packet instance
	 */
	public static Packet9EntityData create(final Entity e) {
		Packet9EntityData pack = new Packet9EntityData();
		pack.defID = e.getEntityDef();
		pack.entID = e.getEntityID();
		pack.layer = e.getLayer();
		pack.loc = e;
		return pack;
	}

	@Override
	public final int getLength() {
		if (loc == null) {
			loc = new Location();
		}
		return (3 * SizeOf.INT) + loc.getLength();
	}
}
