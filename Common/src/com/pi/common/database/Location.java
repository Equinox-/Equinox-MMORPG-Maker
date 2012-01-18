package com.pi.common.database;

import com.pi.common.contants.SectorConstants;

public class Location {
	public int x, z;
	public int plane;

	public Location() {
	}

	public Location(int x, int plane, int z) {
		setLocation(x, plane, z);
	}

	public void setLocation(int x, int plane, int z) {
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

	public int getLocalX() {
		return x - (getSectorX() * SectorConstants.SECTOR_WIDTH);
	}

	public int getGlobalX() {
		return x;
	}

	public int getLocalZ() {
		return z - (getSectorZ() * SectorConstants.SECTOR_HEIGHT);
	}

	public int getGlobalZ() {
		return z;
	}

	public static int dist(Location a, Location b) {
		int dX = a.getGlobalX() - b.getGlobalX();
		int dZ = a.getGlobalZ() - b.getGlobalZ();
		return (int) Math.sqrt((dX * dX) + (dZ * dZ));
	}
}
