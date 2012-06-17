package com.pi.common.contants;

public class SectorConstants {
	public final static int SECTOR_WIDTH = 32;
	public final static int SECTOR_HEIGHT = 32;

	public static int worldToLocalSector(int coord, int sectorSize) {
		return (worldToSector(coord, sectorSize) * -sectorSize) + coord;
	}

	public static int worldToLocalSectorX(int coord) {
		return worldToLocalSector(coord, SECTOR_WIDTH);
	}

	public static int worldToLocalSectorZ(int coord) {
		return worldToLocalSector(coord, SECTOR_HEIGHT);
	}

	public static int worldToSector(int coord, int sectorSize) {
		if (coord < 0)
			coord -= sectorSize - 1;
		return coord / sectorSize;
	}

	public static int worldToSectorX(int coord) {
		return worldToSector(coord, SECTOR_WIDTH);
	}

	public static int worldToSectorZ(int coord) {
		return worldToSector(coord, SECTOR_HEIGHT);
	}

	public static int localSectorToWorld(int sector, int coord, int sectorSize) {
		return (sector * sectorSize) + coord;
	}

	public static int localSectorToWorldX(int sector, int coord) {
		return localSectorToWorld(sector, coord, SECTOR_WIDTH);
	}

	public static int localSectorToWorldZ(int sector, int coord) {
		return localSectorToWorld(sector, coord, SECTOR_HEIGHT);
	}
}
