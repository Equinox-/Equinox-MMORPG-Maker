package com.pi.common.game;

import com.pi.common.contants.Direction;
import com.pi.common.database.Location;
import com.pi.common.database.Tile.TileLayer;

public class Entity extends Location {
    protected Direction dir = Direction.UP;
    protected TileLayer aboveLayer = TileLayer.MASK1;
    protected int entityID = -1;
    protected EntityListener listener = null;
    protected int defID = 0;

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

    public void setEntityDef(int entityDef) {
	this.defID = entityDef;
    }

    public int getEntityDef() {
	return this.defID;
    }

    public boolean setEntityID(int id) {
	if (entityID == -1) {
	    this.entityID = id;
	    return true;
	}
	return false;
    }

    public Direction getDir() {
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
	    listener.entityTeleport(getEntityID(), new Location(getGlobalX(),
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

    public void setDir(Direction dir) {
	this.dir = dir;
    }

    public void doMovement() {
	if (listener != null && getEntityID() != -1)
	    listener.entityMove(getEntityID(), new Location(getGlobalX(),
		    getPlane(), getGlobalZ()), new Location(x + dir.getXOff(),
		    plane, z + dir.getZOff()), dir);
	x += dir.getXOff();
	z += dir.getZOff();
    }

    public void teleportShort(Location apply) {
	// calculate dir
	int xC = apply.x - x;
	int zC = apply.z - z;
	if (xC != 0 || zC != 0)
	    dir = Direction.getBestDirection(xC, zC);
	if (listener != null && getEntityID() != -1)
	    listener.entityMove(getEntityID(), new Location(getGlobalX(),
		    getPlane(), getGlobalZ()), apply, dir);
	this.x = apply.x;
	this.z = apply.z;
    }

    public void unRegister() {
	if (listener != null && getEntityID() != -1)
	    listener.entityDispose(getEntityID());
    }
}
