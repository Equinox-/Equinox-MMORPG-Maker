package com.pi.common.database;

import java.awt.Point;
import java.io.Serializable;

import com.pi.common.contants.GlobalConstants;
import com.pi.common.contants.SectorConstants;

public class Sector implements Serializable {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private int baseX, baseY;
    private Tile[][] tiles = new Tile[SectorConstants.SECTOR_WIDTH][SectorConstants.SECTOR_HEIGHT];
    private int revision = 0;

    public Tile getLocalTile(int x, int y) {
	if (x >= 0 && y >= 0 && x < tiles.length && y < tiles[x].length)
	    return tiles[x][y];
	else
	    return null;
    }

    public void setLocalTile(int x, int y, Tile tile) {
	if (x >= 0 && y >= 0 && x < tiles.length && y < tiles[x].length)
	    tiles[x][y] = tile;
    }

    public boolean isTileInSector(int x, int y) {
	return x >= baseX && y >= baseY && x - baseX < tiles.length
		&& y - baseY < tiles[x - baseX].length;
    }

    public Tile getGlobalTile(int x, int y) {
	return getLocalTile(x - baseX, y - baseY);
    }

    public void setGlobalTile(int x, int y, Tile tile) {
	setLocalTile(x - baseX, y - baseY, tile);
    }

    public void setSectorLocation(int x, int y) {
	baseX = x * SectorConstants.SECTOR_WIDTH;
	baseY = y * SectorConstants.SECTOR_HEIGHT;
    }

    public Point getSectorLocation(Point p) {
	p.setLocation(baseX / SectorConstants.SECTOR_WIDTH, baseY
		/ SectorConstants.SECTOR_HEIGHT);
	return p;
    }

    public Point getSectorLocation() {
	return getSectorLocation(new Point());
    }

    public int getSectorX() {
	return baseX / SectorConstants.SECTOR_WIDTH;
    }

    public int getSectorY() {
	return baseY / SectorConstants.SECTOR_HEIGHT;
    }

    public void setRevision(int revision) {
	this.revision = revision;
    }

    public int getRevision() {
	return revision;
    }
}
