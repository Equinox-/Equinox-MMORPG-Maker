package com.pi.common.database.world;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.contants.SectorConstants;
import com.pi.common.database.Location;

/**
 * Class describing the location of a sector.
 * 
 * @author Westin
 * 
 */
public class SectorLocation {
	/**
	 * The x coordinate.
	 */
	private int x;
	/**
	 * The plane.
	 */
	private int plane;
	/**
	 * The z coordinate.
	 */
	private int z;

	/**
	 * Create a sector location instance at <code>0,0</code> on plane
	 * <code>0</code>.
	 */
	public SectorLocation() {
		this(0, 0, 0);
	}

	/**
	 * Create a sector location instance at the specified coordinates.
	 * 
	 * @param sX the sector's x coordinate
	 * @param sPlane the sector's plane
	 * @param sZ the sector's z coordinate
	 */
	public SectorLocation(final int sX, final int sPlane,
			final int sZ) {
		setLocation(sX, sPlane, sZ);
	}

	/**
	 * Sets the coordinates of this object.
	 * 
	 * @param sX the sector's x coordinate
	 * @param sPlane the sector's plane
	 * @param sZ the sector's z coordinate
	 */
	public final void setLocation(final int sX,
			final int sPlane, final int sZ) {
		this.x = sX;
		this.plane = sPlane;
		this.z = sZ;
	}

	@Override
	public final boolean equals(final Object o) {
		if (o instanceof SectorLocation) {
			SectorLocation l = (SectorLocation) o;
			return l.x == x && l.plane == plane && l.z == z;
		} else if (o instanceof Location) {
			return containsLocation((Location) o);
		}
		return false;
	}

	/**
	 * Gets this sector's x position.
	 * 
	 * @return the x coordinate
	 */
	public final int getSectorX() {
		return x;
	}

	/**
	 * Gets this sector's z position.
	 * 
	 * @return the z coordinate
	 */
	public final int getSectorZ() {
		return z;
	}

	/**
	 * Gets this location's plane.
	 * 
	 * @return the plane
	 */
	public final int getPlane() {
		return plane;
	}

	/**
	 * Gets this sector's x position in world space.
	 * 
	 * @see SectorConstants#localSectorToWorldX(int, int)
	 * @return the x coordinate
	 */
	public final int getGlobalX() {
		return SectorConstants.localSectorToWorldX(x, 0);
	}

	/**
	 * Gets this sector's z position in world space.
	 * 
	 * @see SectorConstants#localSectorToWorldZ(int, int)
	 * @return the z coordinate
	 */
	public final int getGlobalZ() {
		return SectorConstants.localSectorToWorldZ(z, 0);
	}

	/**
	 * Checks if the sector defined by this sector location contains the
	 * provided location.
	 * 
	 * @param l the location to check
	 * @return if this sector contains the location
	 */
	public final boolean containsLocation(final Location l) {
		return l.plane == this.plane && l.getSectorX() == this.x
				&& l.getSectorZ() == this.z;
	}

	@Override
	public final int hashCode() {
		return (x << SizeOf.LONG) ^ (z << SizeOf.INT) ^ plane;
	}

	@Override
	public final String toString() {
		return "SectorLocation[x=" + x + ", z=" + z + ", plane="
				+ plane + "]";
	}
}
