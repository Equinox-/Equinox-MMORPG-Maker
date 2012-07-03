package com.pi.common.contants;

/**
 * Class containing utility methods and constants for sectors.
 * 
 * @author Westin
 * 
 */
public final class SectorConstants {
	/**
	 * Sector width in tiles.
	 */
	public static final int SECTOR_WIDTH = 32;
	/**
	 * Sector height in tiles.
	 */
	public static final int SECTOR_HEIGHT = 32;

	/**
	 * Converts a world coordinate to a local sector coordinate.
	 * 
	 * @param coord the world position
	 * @param sectorSize the sector size on the axis in question
	 * @return the local coordinate
	 */
	public static int worldToLocalSector(final int coord,
			final int sectorSize) {
		return (worldToSector(coord, sectorSize) * -sectorSize)
				+ coord;
	}

	/**
	 * Converts a world x coordinate to the local sector x coordinate.
	 * 
	 * @see SectorConstants#worldToLocalSector(int, int)
	 * @param coord the world x coordinate
	 * @return the local sector x coordinate
	 */
	public static int worldToLocalSectorX(final int coord) {
		return worldToLocalSector(coord, SECTOR_WIDTH);
	}

	/**
	 * Converts a world z coordinate to the local sector z coordinate.
	 * 
	 * @see SectorConstants#worldToLocalSector(int, int)
	 * @param coord the world z coordinate
	 * @return the local sector z coordinate
	 */
	public static int worldToLocalSectorZ(final int coord) {
		return worldToLocalSector(coord, SECTOR_HEIGHT);
	}

	/**
	 * Converts a world coordinate to a sector position.
	 * 
	 * @param coord the world position
	 * @param sectorSize the sector size on the axis in question
	 * @return the local coordinate
	 */
	public static int worldToSector(final int coord,
			final int sectorSize) {
		int coordT = coord;
		if (coordT < 0) {
			coordT -= sectorSize - 1;
		}
		return coordT / sectorSize;
	}

	/**
	 * Converts a world x coordinate to the sector x position.
	 * 
	 * @see SectorConstants#worldToSector(int, int)
	 * @param coord the world x coordinate
	 * @return the sector x position
	 */
	public static int worldToSectorX(final int coord) {
		return worldToSector(coord, SECTOR_WIDTH);
	}

	/**
	 * Converts a world z coordinate to the sector z position.
	 * 
	 * @see SectorConstants#worldToSector(int, int)
	 * @param coord the world z coordinate
	 * @return the sector z position
	 */
	public static int worldToSectorZ(final int coord) {
		return worldToSector(coord, SECTOR_HEIGHT);
	}

	/**
	 * Converts a local sector coordinate and sector position to a world
	 * coordinate.
	 * 
	 * @param sector the sector coordinate
	 * @param coord the local sector coordinate
	 * @param sectorSize the sector size on the axis in question
	 * @return the world coordinate
	 */
	public static int localSectorToWorld(final int sector,
			final int coord, final int sectorSize) {
		return (sector * sectorSize) + coord;
	}

	/**
	 * Converts a local sector x coordinate and sector x position to a world x
	 * coordinate.
	 * 
	 * @param sector the sector coordinate
	 * @param coord the local sector coordinate
	 * @see SectorConstants#localSectorToWorld(int, int, int)
	 * @return the world x coordinate
	 */
	public static int localSectorToWorldX(final int sector,
			final int coord) {
		return localSectorToWorld(sector, coord, SECTOR_WIDTH);
	}

	/**
	 * Converts a local sector z coordinate and sector x position to a world z
	 * coordinate.
	 * 
	 * @param sector the sector coordinate
	 * @param coord the local sector coordinate
	 * @see SectorConstants#localSectorToWorld(int, int, int)
	 * @return the world z coordinate
	 */
	public static int localSectorToWorldZ(final int sector,
			final int coord) {
		return localSectorToWorld(sector, coord, SECTOR_HEIGHT);
	}

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private SectorConstants() {
	}
}
