package com.pi.common.game;

import com.pi.common.contants.Direction;
import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileFlags;
import com.pi.common.database.Location;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.world.SectorManager;

public class Entity extends Location {
	protected Direction dir = Direction.UP;
	protected TileLayer aboveLayer = TileLayer.MASK1;
	protected int entityID = -1;
	protected int defID = 0;

	public Entity(int def) {
		this.defID = def;
	}

	public Entity() {
	}

	public void setEntityDef(int entityDef) {
		this.defID = entityDef;
	}

	public int getEntityDef() {
		return this.defID;
	}

	public boolean setEntityID(int id) {
		if (entityID == -1) {
			this.entityID = id;
			return true;
		}
		return false;
	}

	public Direction getDir() {
		return dir;
	}

	public int getEntityID() {
		return entityID;
	}

	public TileLayer getLayer() {
		return aboveLayer;
	}

	@Override
	public void setLocation(int x, int plane, int z) {
		super.setLocation(x, plane, z);
	}

	public void setLocation(Location l) {
		setLocation(l.x, l.plane, l.z);
	}

	public void setLayer(TileLayer t) {
		aboveLayer = t;
	}

	public void setDir(Direction dir) {
		this.dir = dir;
	}

	public void doMovement() {
		x += dir.getXOff();
		z += dir.getZOff();
	}

	public void teleportShort(Location apply) {
		// calculate dir
		int xC = apply.x - x;
		int zC = apply.z - z;
		if (xC != 0 || zC != 0)
			dir = Direction.getBestDirection(xC, zC);
		this.x = apply.x;
		this.z = apply.z;
	}

	public boolean canMoveIn(SectorManager mgr, Direction dir) {
		Sector sec =
				mgr.getSector(getSectorX(), getPlane(),
						getSectorZ());
		int moveSX =
				SectorConstants
						.worldToSectorX(x + dir.getXOff());
		int moveSZ =
				SectorConstants
						.worldToSectorZ(z + dir.getZOff());
		Sector move;
		if (moveSX != sec.getSectorX()
				|| moveSZ != sec.getSectorZ()) {
			move = mgr.getSector(moveSX, getPlane(), moveSZ);
		} else {
			move = sec;
		}
		if (sec != null && move != null) {
			Tile t = sec.getGlobalTile(x, z);
			if (t != null) {
				switch (dir) {
				case UP:
					return !t.hasFlag(TileFlags.WALL_NORTH);
				case DOWN:
					return !t.hasFlag(TileFlags.WALL_SOUTH);
				case RIGHT:
					return !t.hasFlag(TileFlags.WALL_EAST);
				case LEFT:
					return !t.hasFlag(TileFlags.WALL_WEST);
				}
			}
		}
		return false;
	}

	public boolean canMove(SectorManager sec) {
		return canMoveIn(sec, getDir());
	}
}
