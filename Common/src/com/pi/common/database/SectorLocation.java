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

    public boolean containsLocation(Location l) {
	return l.plane == this.plane && l.getSectorX() == this.x
		&& l.getSectorZ() == this.z;
    }

    public String toString() {
	return "SectorLocation[x=" + x + ", z=" + z + ", plane=" + plane + "]";
    }
}
