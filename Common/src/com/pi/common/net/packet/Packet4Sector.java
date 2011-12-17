package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.GraphicsObject;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet4Sector extends Packet {
    public Sector sector;

    @Override
    protected void writeData(PacketOutputStream o) throws IOException {
	/*
	 * ByteArrayOutputStream out = new ByteArrayOutputStream();
	 * ObjectOutputStream objOut = new ObjectOutputStream(out);
	 * objOut.writeObject(sector); objOut.close(); out.flush();
	 * dOut.writeByteArray(out.toByteArray()); out.close();
	 */
	o.writeInt(sector.getSectorX());
	o.writeInt(sector.getSectorY());
	o.writeInt(sector.getSectorZ());
	o.writeInt(sector.getRevision());
	Tile[][] tArr = sector.getTileArray();
	o.writeInt(tArr.length);
	for (int i = 0; i < tArr.length; i++) {
	    o.writeInt(tArr[i].length);
	    for (int i2 = 0; i2 < tArr[i].length; i2++) {
		Tile t = tArr[i][i2];
		o.writeInt(t.getFlags());
		o.writeInt(t.layerMap().size());
		for (TileLayer lyr : t.layerMap().keySet()) {
		    o.writeInt(lyr.ordinal());
		    GraphicsObject gO = t.layerMap().get(lyr);
		    o.writeInt(gO.getGraphic());
		    o.writeFloat(gO.getPositionX());
		    o.writeFloat(gO.getPositionY());
		    o.writeFloat(gO.getPositionWidth());
		    o.writeFloat(gO.getPositionHeight());
		}
	    }
	}
    }

    @Override
    protected void readData(PacketInputStream o) throws IOException {
	/*
	 * byte[] byts = dIn.readByteArray(); ByteArrayInputStream in = new
	 * ByteArrayInputStream(byts); ObjectInputStream objIn = new
	 * ObjectInputStream(in); try { sector = (Sector) objIn.readObject(); }
	 * catch (ClassNotFoundException e) { sector = null; } objIn.close();
	 * in.close();
	 */
	sector = new Sector();
	sector.setSectorLocation(o.readInt(), o.readInt(), o.readInt());
	sector.setRevision(o.readInt());
	int i1m = o.readInt();
	for (int i = 0; i < i1m; i++) {
	    int i2m = o.readInt();
	    for (int i2 = 0; i2 < i2m; i2++) {
		Tile t = new Tile();
		t.setFlags(o.readInt());
		int layers = o.readInt();
		for (int l = 0; l < layers; l++) {
		    int layer = o.readInt();
		    if (layer >= 0 && layer < TileLayer.values().length) {
			t.setLayer(
				TileLayer.values()[layer],
				new GraphicsObject(o.readInt(), o
					.readFloat(), o.readFloat(), o
					.readFloat(), o.readFloat()));
		    }
		}
		sector.setLocalTile(i, i2, t);
	    }
	}
    }
    @Override
    public int getID() {
	return 4;
    }
}
