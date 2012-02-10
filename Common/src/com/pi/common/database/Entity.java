package com.pi.common.database;

import java.io.IOException;

import com.pi.common.database.Tile.TileLayer;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Entity extends Location {
    private byte dir;
    private TileLayer aboveLayer = TileLayer.MASK1;
    private int entityID = -1;

    public boolean setEntityID(int id) {
	if (entityID == -1) {
	    this.entityID = id;
	    return true;
	}
	return false;
    }

    public byte getDir() {
	return dir;
    }

    public int getEntityID() {
	return entityID;
    }

    public TileLayer getLayer() {
	return aboveLayer;
    }

    public void setLayer(TileLayer l) {
	aboveLayer = l;
    }

    @Override
    public int getLength() {
	return super.getLength() + 9;
    }

    @Override
    public void read(PacketInputStream pIn) throws IOException {
	super.read(pIn);
	int layer = pIn.readInt();
	aboveLayer = (layer >= 0 && layer < TileLayer.values().length) ? TileLayer
		.values()[layer] : null;
	entityID = pIn.readInt();
	dir = pIn.readByte();
    }

    @Override
    public void write(PacketOutputStream pOut) throws IOException {
	super.write(pOut);
	pOut.writeInt(aboveLayer.ordinal());
	pOut.writeInt(entityID);
	pOut.writeByte(dir);
    }
}
