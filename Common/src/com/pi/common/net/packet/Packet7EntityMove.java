package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.game.Entity;
import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet7EntityMove extends Packet {
    public int entityID;
    public Location moved;
    public TileLayer entityLayer;

    public static Packet7EntityMove create(Entity ent) {
	Packet7EntityMove p = new Packet7EntityMove();
	p.entityID = ent.getEntityID();
	p.moved = new Location(ent.getGlobalX(), ent.getPlane(),
		ent.getGlobalZ());
	p.entityLayer = ent.getLayer();
	return p;
    }

    @Override
    protected void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entityID);
	pOut.writeInt(moved.x);
	pOut.writeInt(moved.z);
	pOut.writeInt(moved.plane);
	pOut.writeInt(entityLayer.ordinal());
    }

    @Override
    protected void readData(PacketInputStream pIn) throws IOException {
	entityID = pIn.readInt();
	int x = pIn.readInt();
	int z = pIn.readInt();
	int plane = pIn.readInt();
	moved = new Location(x, plane, z);
	entityLayer = TileLayer.values()[pIn.readInt()];
    }
    @Override
    public int getID() {
	return 7;
    }
}
