package com.pi.common.database;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import com.pi.common.game.ObjectHeap;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Tile implements DatabaseObject {
    private int flags = 0;
    private ObjectHeap<GraphicsObject> layers = new ObjectHeap<GraphicsObject>(
	    0, 1);

    public ObjectHeap<GraphicsObject> layerMap() {
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
	layers.set(layer, tile);
    }

    public GraphicsObject getLayer(int layer) {
	return layers.get(layer);
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
	pOut.writeInt(layers.numElements());
	Iterator<Entry<Integer, GraphicsObject>> iterator = layers.iterator();
	while (iterator.hasNext()) {
	    Entry<Integer, GraphicsObject> ent = iterator.next();
	    if (ent == null || ent.getValue() == null)
		break;
	    pOut.writeInt(ent.getKey());
	    ent.getValue().write(pOut);
	}
    }

    @Override
    public void read(PacketInputStream pIn) throws IOException {
	flags = pIn.readInt();
	int size = pIn.readInt();
	for (int i = 0; i < size; i++) {
	    int layer = pIn.readInt();
	    if (layer >= TileLayer.MAX_VALUE)
		continue;
	    GraphicsObject obj = new GraphicsObject();
	    obj.read(pIn);
	    layers.set(layer, obj);
	}
    }
}
