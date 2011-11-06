package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.game.Entity;
import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet9EntityData extends Packet {
    public Location loc;
    public TileLayer layer;
    public int defID;
    public int entID;

    @Override
    protected void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entID);
	pOut.writeFloat(loc.getGlobalX());
	pOut.writeInt(loc.getPlane());
	pOut.writeFloat(loc.getGlobalZ());
	pOut.writeInt(layer.ordinal());
	pOut.writeInt(defID);
    }

    @Override
    protected void readData(PacketInputStream pIn) throws IOException {
	entID = pIn.readInt();
	loc = new Location(pIn.readFloat(), pIn.readInt(), pIn.readFloat());
	int layerOrd = pIn.readInt();
	if (layerOrd >= 0 && layerOrd < TileLayer.values().length)
	    layer = TileLayer.values()[layerOrd];
	else
	    layer = TileLayer.MASK1;
	defID = pIn.readInt();
    }
    
    public static Packet9EntityData create(Entity e){
	Packet9EntityData pack = new Packet9EntityData();
	pack.defID = e.getEntityDef();
	pack.entID = e.getEntityID();
	pack.layer = e.getLayer();
	pack.loc = e;
	return pack;
    }
}
