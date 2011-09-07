package com.pi.common.game;

import com.pi.common.contants.GlobalConstants;
import com.pi.common.database.*;
import com.pi.common.database.Tile.TileLayer;

public class Entity extends Location {
    public static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private byte dir;
    private TileLayer aboveLayer = TileLayer.MASK1;
    private int entityID = -1;
    private EntityListener listener = null;

    public Entity(EntityDef def) {
	setLocation(def.getGlobalX(), def.getPlane(), def.getGlobalZ());
    }

    public Entity(EntityListener listen, EntityDef def) {
	this(listen);
	setLocation(def.getGlobalX(), def.getPlane(), def.getGlobalZ());
    }

    public Entity() {
    }

    public Entity(EntityListener listen) {
	this.listener = listen;
    }

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

    public void setListener(EntityListener l) {
	this.listener = l;
    }

    @Override
    public void setLocation(float x, int plane, float z) {
	if (listener != null && getEntityID() != -1)
	    listener.entityMove(getEntityID(), new Location(getGlobalX(),
		    getPlane(), getGlobalZ()), new Location(x, plane, z));
	super.setLocation(x, plane, z);
    }

    public void setLayer(TileLayer t) {
	if (listener != null && getEntityID() != -1)
	    listener.entityLayerChange(getEntityID(), getLayer(), t);
	aboveLayer = t;
    }
}
