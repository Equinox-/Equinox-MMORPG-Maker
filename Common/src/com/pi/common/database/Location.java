package com.pi.common.database;

import com.pi.common.contants.SectorConstants;

public class Location {
    public float x, z;
    public int plane;

    public Location() {
    }

    public Location(float x, int plane, float z) {
	setLocation(x, plane, z);
    }

    public void setLocation(float x, int plane, float z) {
	this.x = x;
	this.plane = plane;
	this.z = z;
    }

    public SectorLocation getSectorLocation() {
	return new SectorLocation((int) (x / SectorConstants.SECTOR_WIDTH),
		plane, (int) (z / SectorConstants.SECTOR_HEIGHT));
    }

    public int getSectorX() {
	return (int) (x / SectorConstants.SECTOR_WIDTH);
    }

    public int getPlane() {
	return (int) plane;
    }

    public int getSectorZ() {
	return (int) (z / SectorConstants.SECTOR_HEIGHT);
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof Location) {
	    Location l = (Location) o;
	    return l.x == x && l.plane == plane && l.z == z;
	}
	return false;
    }

    public float getLocalX() {
	return x - (getSectorX() * SectorConstants.SECTOR_WIDTH);
    }

    public float getLocalY() {
	return z - (getSectorZ() * SectorConstants.SECTOR_HEIGHT);
    }
}
