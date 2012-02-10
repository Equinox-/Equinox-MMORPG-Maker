package com.pi.common.database;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Tile implements DatabaseObject {
    private int flags = 0;
    private GraphicsObject[] layers = new GraphicsObject[TileLayer.MAX_VALUE];

    public GraphicsObject[] layerMap() {
	return layers;
    }

    public int getFlags() {
	return flags;
    }

    public void setFlags(int flags) {
	this.flags = flags;
    }

    public boolean hasFlag(int flag) {
	return (flags & flag) == flag;
    }

    public void setLayer(int layer, GraphicsObject tile) {
	layers[layer] = tile;
    }

    public GraphicsObject getLayer(int layer) {
	return layers[layer];
    }

    public static class TileLayer {
	public static final int GROUND = 0;
	public static final int MASK1 = 1;
	public static final int FRINGE1 = 2;
	public static final int MAX_VALUE = 3;
    }

    @Override
    public void write(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(flags);
	for (int i = 0; i < layers.length; i++) {
	    if (layers[i] == null) {
		pOut.writeBoolean(false);
	    } else {
		pOut.writeBoolean(true);
		layers[i].write(pOut);
	    }
	}
    }

    @Override
    public void read(PacketInputStream pIn) throws IOException {
	flags = pIn.readInt();
	for (int i = 0; i < layers.length; i++) {
	    if (pIn.readBoolean()) {
		if (layers[i] == null)
		    layers[i] = new GraphicsObject();
		layers[i].read(pIn);
	    } else {
		layers[i] = null;
	    }
	}
    }
}
