package com.pi.common.database;

import java.awt.Point;

import com.pi.common.contants.GlobalConstants;
import com.pi.common.contants.SectorConstants;
import com.pi.common.database.Tile.TileLayer;

public class Entity {
    private int x, y;
    private byte dir;
    private TileLayer aboveLayer = TileLayer.MASK1;

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public byte getDir() {
	return dir;
    }

    public TileLayer getLayer() {
	return aboveLayer;
    }

    public void setLayer(TileLayer l) {
	aboveLayer = l;
    }

    public void moveTo(int x, int y) {
	if (Math.abs(this.x - x) > Math.abs(this.y - y)) {
	    if (this.x < x) {
		dir = GlobalConstants.DIR_RIGHT;
	    } else {
		dir = GlobalConstants.DIR_LEFT;
	    }
	} else {
	    if (this.y < y) {
		dir = GlobalConstants.DIR_DOWN;
	    } else {
		dir = GlobalConstants.DIR_UP;
	    }
	}
	this.x = x;
	this.y = y;
    }

    public Point getSector() {
	return new Point(x / SectorConstants.SECTOR_WIDTH, y
		/ SectorConstants.SECTOR_HEIGHT);
    }

    public int getLocalX() {
	return x
		- (((int) (x / SectorConstants.SECTOR_WIDTH)) * SectorConstants.SECTOR_WIDTH);
    }

    public int getLocalY() {
	return y
		- (((int) (y / SectorConstants.SECTOR_HEIGHT)) * SectorConstants.SECTOR_HEIGHT);
    }
}
