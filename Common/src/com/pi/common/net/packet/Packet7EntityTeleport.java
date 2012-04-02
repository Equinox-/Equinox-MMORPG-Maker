package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.game.Entity;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet7EntityTeleport extends Packet {
    public int entityID;
    public Location moved;
    public TileLayer entityLayer;

    public static Packet7EntityTeleport create(Entity ent) {
	Packet7EntityTeleport p = new Packet7EntityTeleport();
	p.entityID = ent.getEntityID();
	p.moved = new Location(ent.getGlobalX(), ent.getPlane(),
		ent.getGlobalZ());
	p.entityLayer = ent.getLayer();
	return p;
    }

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	if (moved == null)
	    moved = new Location();
	pOut.writeInt(entityID);
	moved.writeData(pOut);
	pOut.writeInt(entityLayer.ordinal());
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	entityID = pIn.readInt();
	if (moved == null)
	    moved = new Location();
	moved.readData(pIn);
	int eLI = pIn.readInt();
	if (eLI >= 0 && eLI < TileLayer.MAX_VALUE.ordinal())
	    entityLayer = TileLayer.values()[eLI];
	else
	    entityLayer = TileLayer.MASK1;
    }

    @Override
    public int getID() {
	return 7;
    }

    @Override
    public int getLength() {
	if (moved == null)
	    moved = new Location();
	return 8 + moved.getLength();
    }
}
