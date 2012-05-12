package com.pi.common.database;

import java.io.IOException;

import com.pi.common.contants.SectorConstants;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

public class Sector implements PacketObject {
    private int baseX, baseY, baseZ;
    private Tile[][] tiles = new Tile[SectorConstants.SECTOR_WIDTH][SectorConstants.SECTOR_HEIGHT];
    private int revision = 0;

    public Sector() {
	for (int x = 0; x < SectorConstants.SECTOR_WIDTH; x++) {
	    for (int y = 0; y < SectorConstants.SECTOR_HEIGHT; y++) {
		tiles[x][y] = new Tile();
	    }
	}
    }

    public Tile[][] getTileArray() {
	return tiles;
    }

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

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(baseX);
	pOut.writeInt(baseY);
	pOut.writeInt(baseZ);
	pOut.writeInt(revision);
	for (int x = 0; x < tiles.length; x++) {
	    for (int y = 0; y < tiles[x].length; y++) {
		if (tiles[x][y] == null)
		    tiles[x][y] = new Tile();
		tiles[x][y].writeData(pOut);
	    }
	}
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	baseX = pIn.readInt();
	baseY = pIn.readInt();
	baseZ = pIn.readInt();
	revision = pIn.readInt();
	for (int x = 0; x < tiles.length; x++) {
	    for (int y = 0; y < tiles[x].length; y++) {
		if (tiles[x][y] == null)
		    tiles[x][y] = new Tile();
		tiles[x][y].readData(pIn);
	    }
	}
    }

    @Override
    public int getLength() {
	int size = 16;
	for (int x = 0; x < tiles.length; x++) {
	    for (int y = 0; y < tiles[x].length; y++) {
		if (tiles[x][y] == null)
		    tiles[x][y] = new Tile();
		size += tiles[x][y].getLength();
	    }
	}
	return size;
    }
}
