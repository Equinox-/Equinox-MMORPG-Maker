package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.game.Entity;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet9EntityData extends Packet {
	public Location loc;
	public TileLayer layer;
	public int defID;
	public int entID;

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeInt(entID);
		if (loc == null)
			loc = new Location();
		loc.writeData(pOut);
		pOut.writeInt(layer.ordinal());
		pOut.writeInt(defID);
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		entID = pIn.readInt();
		if (loc == null)
			loc = new Location();
		loc.readData(pIn);
		int lI = pIn.readInt();
		if (lI >= 0 && lI < TileLayer.MAX_VALUE.ordinal())
			layer = TileLayer.values()[lI];
		else
			layer = TileLayer.MASK1;
		defID = pIn.readInt();
	}

	public static Packet9EntityData create(Entity e) {
		Packet9EntityData pack = new Packet9EntityData();
		pack.defID = e.getEntityDef();
		pack.entID = e.getEntityID();
		pack.layer = e.getLayer();
		pack.loc = e;
		return pack;
	}

	@Override
	public int getID() {
		return 9;
	}

	@Override
	public int getLength() {
		if (loc == null)
			loc = new Location();
		return 12 + loc.getLength();
	}
}
