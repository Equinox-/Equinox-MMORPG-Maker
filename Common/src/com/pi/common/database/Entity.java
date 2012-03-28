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
    public void readData(PacketInputStream pIn) throws IOException {
	super.readData(pIn);
	int abL = pIn.readInt();
	if (abL >= 0 && abL < TileLayer.MAX_VALUE.ordinal())
	    aboveLayer = TileLayer.values()[abL];
	entityID = pIn.readInt();
	dir = pIn.readByte();
    }

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	super.writeData(pOut);
	pOut.writeInt(aboveLayer.ordinal());
	pOut.writeInt(entityID);
	pOut.writeByte(dir);
    }
}
