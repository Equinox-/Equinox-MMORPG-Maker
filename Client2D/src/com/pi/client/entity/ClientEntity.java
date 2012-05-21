package com.pi.client.entity;

import com.pi.common.contants.Direction;
import com.pi.common.contants.MovementConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.game.Entity;

public class ClientEntity extends Entity {
	private int xOff, zOff;
	private float movementPercent = 0;
	private long moveStart = -1;

	public ClientEntity() {
	}

	public ClientEntity(Entity e) {
		setLocation(e);
		aboveLayer = e.getLayer();
		defID = e.getEntityDef();
		dir = e.getDir();
		entityID = e.getEntityID();
	}

	public void processMovement() {
		if (isMoving()) {
			long elapsed = System.currentTimeMillis() - moveStart;
			movementPercent = (((float) elapsed) / ((float) MovementConstants.WALK_TIME));
			if (movementPercent < 1f) {
				xOff = -dir.getXOff()
						* Math.round((1f - movementPercent)
								* TileConstants.TILE_WIDTH);
				zOff = -dir.getZOff()
						* Math.round((1f - movementPercent)
								* TileConstants.TILE_HEIGHT);
			} else {
				movementPercent = 0f;
				moveStart = -1;
				xOff = 0;
				zOff = 0;
			}
		}
	}

	@Override
	public void doMovement() {
		if (!isMoving()) {
			super.doMovement();
			xOff = 0;
			zOff = 0;
			moveStart = System.currentTimeMillis();
		}
	}

	public void forceStartMoveLoop() {
		xOff = 0;
		zOff = 0;
		moveStart = System.currentTimeMillis();
	}

	@Override
	public void setDir(Direction dir) {
		super.setDir(dir);
	}

	public boolean isMoving() {
		return moveStart > 0;
	}

	public int getXOff() {
		return xOff;
	}

	public int getZOff() {
		return zOff;
	}

	public float getMovementPercent() {
		return movementPercent;
	}
}
