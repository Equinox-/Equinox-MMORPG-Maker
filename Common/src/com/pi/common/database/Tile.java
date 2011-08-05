package com.pi.common.database;

import java.io.Serializable;
import java.util.*;

import com.pi.common.contants.GlobalConstants;

public class Tile implements Serializable {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private int flags = 0;
    private Map<TileLayer, GraphicsObject> layers = new HashMap<TileLayer, GraphicsObject>();

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
}
