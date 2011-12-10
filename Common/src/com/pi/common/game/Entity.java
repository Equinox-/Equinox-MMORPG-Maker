package com.pi.common.game;

import com.pi.common.contants.GlobalConstants;
import com.pi.common.database.Location;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.database.def.EntityDef;

public class Entity extends Location {
    public static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private byte dir;
    private TileLayer aboveLayer = TileLayer.MASK1;
    private int entityID = -1;
    private EntityListener listener = null;
    private int defID = 0;

    public Entity(int def) {
	this.defID = def;
    }

    public Entity(EntityListener listen, int def) {
	this(listen);
	this.defID = def;
    }

    public Entity() {
    }

    public Entity(EntityListener listen) {
	this.listener = listen;
    }
    
    public void setEntityDef(int entityDef){
	this.defID = entityDef;
    }
    
    public int getEntityDef(){
	return this.defID;
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
    public void setLocation(int x, int plane, int z) {
	if (listener != null && getEntityID() != -1)
	    listener.entityMove(getEntityID(), new Location(getGlobalX(),
		    getPlane(), getGlobalZ()), new Location(x, plane, z));
	super.setLocation(x, plane, z);
    }

    public void setLocation(Location l) {
	setLocation(l.x, l.plane, l.z);
    }

    public void setLayer(TileLayer t) {
	if (listener != null && getEntityID() != -1)
	    listener.entityLayerChange(getEntityID(), getLayer(), t);
	aboveLayer = t;
    }
}
