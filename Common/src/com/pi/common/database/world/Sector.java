package com.pi.common.database.world;

import java.io.IOException;

import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.constants.SectorConstants;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * A class for storing sector data.
 * 
 * @author Westin
 * 
 */
public class Sector implements PacketObject {
	/**
	 * The world x position of the local tile at <code>0,0</code>.
	 */
	private int baseX;
	/**
	 * The plane of this sector.
	 */
	private int plane;
	/**
	 * The world z position of the local tile at <code>0,0</code>.
	 */
	private int baseZ;
	/**
	 * The tile array for this sector.
	 */
	private Tile[][] tiles =
			new Tile[SectorConstants.SECTOR_WIDTH][SectorConstants.SECTOR_HEIGHT];
	/**
	 * This sector's revision.
	 */
	private int revision = 0;

	/**
	 * Creates an empty sector.
	 */
	public Sector() {
		for (int x = 0; x < SectorConstants.SECTOR_WIDTH; x++) {
			for (int y = 0; y < SectorConstants.SECTOR_HEIGHT; y++) {
				tiles[x][y] = new Tile();
			}
		}
	}

	/**
	 * Gets the backing array for this sector.
	 * 
	 * @return the tile array
	 */
	public final Tile[][] getTileArray() {
		return tiles;
	}

	/**
	 * Gets the tile at the given position.
	 * 
	 * @param x the tile x
	 * @param z the tile z
	 * @return the tile at the position or <code>null</code> if not in bounds
	 */
	public final Tile getLocalTile(final int x, final int z) {
		if (x >= 0 && z >= 0 && x < tiles.length
				&& z < tiles[x].length) {
			return tiles[x][z];
		} else {
			return null;
		}
	}

	/**
	 * Sets the tile at the given position.
	 * 
	 * @param x the tile x
	 * @param z the tile z
	 * @param tile the tile to assign to the given position
	 */
	public final void setLocalTile(final int x, final int z,
			final Tile tile) {
		if (x >= 0 && z >= 0 && x < tiles.length
				&& z < tiles[x].length) {
			tiles[x][z] = tile;
		}
	}

	/**
	 * Checks if the global position <code>x,z</code> is in this sector.
	 * 
	 * @param x the global x coordinate
	 * @param z the global z coordinate
	 * @return <code>true</code> if inside this sector, <code>false</code> if
	 *         not.
	 */
	public final boolean isTileInSector(final int x, final int z) {
		return x >= baseX && z >= baseZ
				&& x - baseX < tiles.length
				&& z - baseZ < tiles[x - baseX].length;
	}

	/**
	 * Gets the tile in this sector at the global position defined by
	 * <code>x,z</code>.
	 * 
	 * @see Sector#getLocalTile(int, int)
	 * @param x the global x coordinate
	 * @param z the global z coordinate
	 * @return the local tile, or <code>null</code> if out of bounds.
	 */
	public final Tile getGlobalTile(final int x, final int z) {
		return getLocalTile(x - baseX, z - baseZ);
	}

	/**
	 * Sets the tile in this sector at the global position defined by
	 * <code>x,z</code>.
	 * 
	 * @see Sector#setLocalTile(int, int, Tile)
	 * @param x the global x coordinate
	 * @param z the global z coordinate
	 * @param tile the tile to set
	 */
	public final void setGlobalTile(final int x, final int z,
			final Tile tile) {
		setLocalTile(x - baseX, z - baseZ, tile);
	}

	/**
	 * Sets this sector's location.
	 * 
	 * @param x the sector's x coordinate
	 * @param sPlane the sector's plane
	 * @param z the sector's z coordinate
	 */
	public final void setSectorLocation(final int x,
			final int sPlane, final int z) {
		baseX = SectorConstants.localSectorToWorldX(x, 0);
		plane = sPlane;
		baseZ = SectorConstants.localSectorToWorldZ(z, 0);
	}

	/**
	 * Gets this sector's x position.
	 * 
	 * @see SectorConstants#worldToSectorX(int)
	 * @return this sector's x position
	 */
	public final int getSectorX() {
		return SectorConstants.worldToSectorX(baseX);
	}

	/**
	 * Gets this sector's z position.
	 * 
	 * @see SectorConstants#worldToSectorZ(int)
	 * @return this sector's z position
	 */
	public final int getSectorZ() {
		return SectorConstants.worldToSectorZ(baseZ);
	}

	/**
	 * Gets this sector's plane.
	 * 
	 * @return this sector's plane
	 */
	public final int getPlane() {
		return plane;
	}

	/**
	 * Gets this sector's location.
	 * 
	 * @see Sector#getSectorX()
	 * @see Sector#getSectorZ()
	 * @return this sector's location
	 */
	public final SectorLocation getSectorLocation() {
		return new SectorLocation(getSectorX(), getPlane(),
				getSectorZ());
	}

	/**
	 * Sets this sector's revision.
	 * 
	 * @param sRevision the new revision
	 */
	public final void setRevision(final int sRevision) {
		this.revision = sRevision;
	}

	/**
	 * Gets this sector's revision.
	 * 
	 * @return this sector's revision
	 */
	public final int getRevision() {
		return revision;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(baseX);
		pOut.writeInt(plane);
		pOut.writeInt(baseZ);
		pOut.writeInt(revision);
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				if (tiles[x][y] == null) {
					tiles[x][y] = new Tile();
				}
				tiles[x][y].writeData(pOut);
			}
		}
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		baseX = pIn.readInt();
		plane = pIn.readInt();
		baseZ = pIn.readInt();
		revision = pIn.readInt();
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				if (tiles[x][y] == null) {
					tiles[x][y] = new Tile();
				}
				tiles[x][y].readData(pIn);
			}
		}
	}

	@Override
	public final int getLength() {
		int size = 4 * SizeOf.INT;
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				if (tiles[x][y] == null) {
					tiles[x][y] = new Tile();
				}
				size += tiles[x][y].getLength();
			}
		}
		return size;
	}
}
