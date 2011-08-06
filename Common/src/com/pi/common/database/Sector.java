package com.pi.common.database;

import java.awt.Point;
import java.io.Serializable;

import com.pi.common.contants.GlobalConstants;
import com.pi.common.contants.SectorConstants;

public class Sector implements Serializable {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private int baseX, baseY, baseZ;
    private Tile[][] tiles = new Tile[SectorConstants.SECTOR_WIDTH][SectorConstants.SECTOR_HEIGHT];
    private int revision = 0;

    public Tile getLocalTile(int x, int z) {
	if (x >= 0 && z >= 0 && x < tiles.length && z < tiles[x].length)
	    return tiles[x][z];
	else
	    return null;
    }

    public void setLocalTile(int x, int z, Tile tile) {
	if (x >= 0 && z >= 0 && x < tiles.length && z < tiles[x].length)
	    tiles[x][z] = tile;
    }

    public boolean isTileInSector(int x, int z) {
	return x >= baseX && z >= baseZ && x - baseX < tiles.length
		&& z - baseZ < tiles[x - baseX].length;
    }

    public Tile getGlobalTile(int x, int z) {
	return getLocalTile(x - baseX, z - baseZ);
    }

    public void setGlobalTile(int x, int z, Tile tile) {
	setLocalTile(x - baseX, z - baseZ, tile);
    }

    public void setSectorLocation(int x, int y, int z) {
	baseX = x * SectorConstants.SECTOR_WIDTH;
	baseY = y;
	baseZ = z * SectorConstants.SECTOR_HEIGHT;
    }

    public int getSectorX() {
	return baseX / SectorConstants.SECTOR_WIDTH;
    }

    public int getSectorZ() {
	return baseZ / SectorConstants.SECTOR_HEIGHT;
    }

    public int getSectorY() {
	return baseY;
    }

    public SectorLocation getSectorLocation() {
	return new SectorLocation(getSectorX(), getSectorY(), getSectorZ());
    }

    public void setRevision(int revision) {
	this.revision = revision;
    }

    public int getRevision() {
	return revision;
    }
}
