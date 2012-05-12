package com.pi.common.database;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

public class Tile implements PacketObject {
    private int flags = 0;
    private GraphicsObject[] layers = new GraphicsObject[TileLayer.MAX_VALUE
	    .ordinal()];

    public int getFlags() {
	return flags;
    }

    public void setFlags(int flags) {
	this.flags = flags;
    }

    public boolean hasFlag(int flag) {
	return (flags & flag) == flag;
    }

    public void setLayer(TileLayer layer, GraphicsObject tile) {
	layers[layer.ordinal()] = tile;
    }

    public GraphicsObject getLayer(TileLayer layer) {
	return layer.ordinal() < layers.length ? layers[layer.ordinal()] : null;
    }

    public static enum TileLayer {
	GROUND, MASK1, FRINGE1, MAX_VALUE
    }

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(flags);
	int layerFlags = 0;
	int stuff = 1;
	for (int i = 0; i < layers.length; i++) {
	    if (layers[i] != null) {
		layerFlags ^= stuff;
	    }
	    stuff = stuff << 1;
	}
	pOut.writeInt(layerFlags);
	for (int i = 0; i < layers.length; i++) {
	    if (layers[i] != null)
		layers[i].writeData(pOut);
	}
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	flags = pIn.readInt();
	int layerFlags = pIn.readInt();
	int stuff = 1;
	for (int i = 0; i < layers.length; i++) {
	    if ((layerFlags & stuff) == stuff) {
		if (layers[i] == null)
		    layers[i] = new GraphicsObject();
		layers[i].readData(pIn);
	    } else {
		layers[i] = null;
	    }
	    stuff = stuff << 1;
	}
    }

    @Override
    public int getLength() {
	int size = 8;
	for (int i = 0; i < layers.length; i++) {
	    if (layers[i] != null)
		size += layers[i].getLength();
	}
	return size;
    }
}
