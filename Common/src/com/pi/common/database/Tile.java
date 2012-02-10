package com.pi.common.database;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Tile implements DatabaseObject {
    private int flags = 0;
    private Map<TileLayer, GraphicsObject> layers = new HashMap<TileLayer, GraphicsObject>();

    public Map<TileLayer, GraphicsObject> layerMap() {
	return Collections.unmodifiableMap(layers);
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

    public void setLayer(TileLayer layer, GraphicsObject tile) {
	layers.put(layer, tile);
    }

    public GraphicsObject getLayer(TileLayer layer) {
	return layers.get(layer);
    }

    public static enum TileLayer {
	GROUND, MASK1, FRINGE1;
    }

    @Override
    public void write(PacketOutputStream pOut) throws IOException {
	// TODO Auto-generated method stub

    }

    @Override
    public void read(PacketInputStream pIn) throws IOException {
	// TODO Auto-generated method stub

    }

    @Override
    public int getLength() {
	int layerSize = 0;
	for (GraphicsObject o : layers.values())
	    layerSize += 4 + o.getLength();
	return layerSize + 4;
    }
}
