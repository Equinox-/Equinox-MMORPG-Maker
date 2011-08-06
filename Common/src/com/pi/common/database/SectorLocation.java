package com.pi.common.database;

public class SectorLocation {
    public int x, y, z;

    public SectorLocation() {
    }

    public SectorLocation(int x, int y, int z) {
	setLocation(x, y, z);
    }

    public void setLocation(int x, int y, int z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    public boolean equals(Object o) {
	if (o instanceof SectorLocation) {
	    SectorLocation l = (SectorLocation) o;
	    return l.x == x && l.y == y && l.z == z;
	}
	return false;
    }
}
