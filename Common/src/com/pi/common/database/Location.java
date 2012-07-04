package com.pi.common.database;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.contants.SectorConstants;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * Class representing a location in the world.
 * 
 * @author Westin
 * 
 */
public class Location implements PacketObject {
	/**
	 * The x coordinate.
	 */
	public int x;
	/**
	 * the z coordinate.
	 */
	public int z;
	/**
	 * The location's plane.
	 */
	public int plane;

	/**
	 * Creates a location with the position of <code>0,0,0</code>.
	 */
	public Location() {
		this(0, 0, 0);
	}

	/**
	 * Creates a location with the given x and z coordinates, on the provided
	 * plane.
	 * 
	 * @param sX the x position
	 * @param sPlane the plane
	 * @param sZ the z position
	 */
	public Location(final int sX, final int sPlane, final int sZ) {
		setLocation(sX, sPlane, sZ);
	}

	/**
	 * Sets all the coordinates of this locations.
	 * 
	 * @param sX the x coordinate
	 * @param sPlane the plane
	 * @param sZ the z coordinate
	 */
	public final void setLocation(final int sX,
			final int sPlane, final int sZ) {
		this.x = sX;
		this.plane = sPlane;
		this.z = sZ;
	}

	/**
	 * Gets the position of the sector containing this location.
	 * 
	 * @see Location#getSectorX()
	 * @see Location#getSectorZ()
	 * @return the sector location
	 */
	public final SectorLocation getSectorLocation() {
		return new SectorLocation(getSectorX(), plane,
				getSectorZ());
	}

	/**
	 * Gets the x position of the sector containing this location.
	 * 
	 * @see SectorConstants#worldToSectorX(int)
	 * @return the sector's x position
	 */
	public final int getSectorX() {
		return SectorConstants.worldToSectorX(x);
	}

	/**
	 * Gets the plane of this location.
	 * 
	 * @return the plane
	 */
	public final int getPlane() {
		return (int) plane;
	}

	/**
	 * Gets the z position of the sector containing this location.
	 * 
	 * @see SectorConstants#worldToSectorZ(int)
	 * @return the sector's z position
	 */
	public final int getSectorZ() {
		return SectorConstants.worldToSectorZ(z);
	}

	@Override
	public final int hashCode() {
		return (x << SizeOf.LONG) ^ (z << SizeOf.INT) ^ plane;
	}

	@Override
	public final boolean equals(final Object o) {
		if (o instanceof Location) {
			Location l = (Location) o;
			return l.x == x && l.plane == plane && l.z == z;
		}
		return false;
	}

	/**
	 * Gets the local sector x coordinate of this location.
	 * 
	 * @see SectorConstants#worldToLocalSectorX(int)
	 * @return the local x coordinate
	 */
	public final int getLocalX() {
		return SectorConstants.worldToLocalSectorX(x);
	}

	/**
	 * Gets the global x coordinate of this location.
	 * 
	 * @return the global x coordinate
	 */
	public final int getGlobalX() {
		return x;
	}

	/**
	 * Gets the local sector z coordinate of this location.
	 * 
	 * @see SectorConstants#worldToLocalSectorZ(int)
	 * @return the local z coordinate
	 */
	public final int getLocalZ() {
		return SectorConstants.worldToLocalSectorZ(z);
	}

	/**
	 * Gets the global z coordinate of this location.
	 * 
	 * @return the global z coordinate
	 */
	public final int getGlobalZ() {
		return z;
	}

	/**
	 * Computes the distance between two locations, assuming they are on the
	 * same plane.
	 * 
	 * @param a the first location
	 * @param b the second location
	 * @return the distance
	 */
	public static int dist(final Location a, final Location b) {
		int dX = a.getGlobalX() - b.getGlobalX();
		int dZ = a.getGlobalZ() - b.getGlobalZ();
		return (int) Math.sqrt((dX * dX) + (dZ * dZ));
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(x);
		pOut.writeInt(z);
		pOut.writeInt(plane);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		x = pIn.readInt();
		z = pIn.readInt();
		plane = pIn.readInt();
	}

	@Override
	public final int getLength() {
		return 3 * SizeOf.INT;
	}
}
