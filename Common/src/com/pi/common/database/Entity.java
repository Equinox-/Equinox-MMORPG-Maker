package com.pi.common.database;

import com.pi.common.database.Tile.TileLayer;

public class Entity extends Location {
    private byte dir;
    private TileLayer aboveLayer = TileLayer.MASK1;

    public byte getDir() {
	return dir;
    }

    public TileLayer getLayer() {
	return aboveLayer;
    }

    public void setLayer(TileLayer l) {
	aboveLayer = l;
    }
}
