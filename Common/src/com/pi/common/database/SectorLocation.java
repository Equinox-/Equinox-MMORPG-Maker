package com.pi.common.database;

public class SectorLocation {
    public int x, plane, z;

    public SectorLocation() {
    }

    public SectorLocation(int x, int plane, int z) {
	setLocation(x, plane, z);
    }

    public void setLocation(int x, int plane, int z) {
	this.x = x;
	this.plane = plane;
	this.z = z;
    }

    @Override
    public boolean equals(Object o) {
	if (o instanceof SectorLocation) {
	    SectorLocation l = (SectorLocation) o;
	    return l.x == x && l.plane == plane && l.z == z;
	}
	return false;
    }
}
