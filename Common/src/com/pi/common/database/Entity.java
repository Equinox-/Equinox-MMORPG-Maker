package com.pi.common.database;

import java.io.Serializable;

import com.pi.common.contants.GlobalConstants;
import com.pi.common.database.Tile.TileLayer;

public class Entity extends Location implements Serializable {
	private static final long serialVersionUID = GlobalConstants.serialVersionUID;
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
}
