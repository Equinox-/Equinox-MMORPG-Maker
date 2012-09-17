package com.pi.client.entity;

import com.pi.common.constants.EntityConstants;
import com.pi.common.constants.TileConstants;
import com.pi.common.game.entity.Entity;
import com.pi.common.game.entity.EntityContainer;

/**
 * An entity container class that manages special variables for client entities.
 * 
 * @author Westin
 * 
 */
public class ClientEntity extends EntityContainer {
	/**
	 * The pixel xOff and zOff for smooth movement.
	 */
	private int xOff, zOff;
	/**
	 * The scalar value of how far the entity has traveled to the next tile.
	 */
	private float movementPercent = 0;
	/**
	 * What time (in milliseconds) this entity started moving at.
	 */
	private long moveStart = -1;

	/**
	 * How long it should take this entity to move one tile.
	 */
	private long movementTime = EntityConstants.WALK_TIME;

	/**
	 * Creates a client entity that wraps the provided entity.
	 * 
	 * @param e
	 *            the entity to wrap
	 */
	public ClientEntity(final Entity e) {
		super(e);
	}

	/**
	 * Process the movement loop.
	 */
	public final void processMovement() {
		if (isMoving()) {
			long elapsed = System.currentTimeMillis() - moveStart;
			movementPercent = (((float) elapsed) / ((float) movementTime));
			if (movementPercent < 1f) {
				xOff = -getWrappedEntity().getDir().getXOff()
						* Math.round((1f - movementPercent)
								* TileConstants.TILE_WIDTH);
				zOff = -getWrappedEntity().getDir().getZOff()
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

	/**
	 * Do entity movement for this client entity and start the movement loop.
	 * 
	 * @param isRunning
	 *            if this should be a running movement
	 */
	public final void doMovement(final boolean isRunning) {
		if (!isMoving()) {
			if (isRunning) {
				movementTime = EntityConstants.RUN_TIME;
			} else {
				movementTime = EntityConstants.WALK_TIME;
			}
			getWrappedEntity().doMovement();
			moveStart = System.currentTimeMillis();
			processMovement();
		}
	}

	/**
	 * Checks if this client entity is moving.
	 * 
	 * @return if this entity is moving
	 */
	public final boolean isMoving() {
		return moveStart > 0;
	}

	/**
	 * Gets the current x offset.
	 * 
	 * @return the x offset
	 */
	public final int getXOff() {
		return xOff;
	}

	/**
	 * Gets the current z offset.
	 * 
	 * @return the z offset
	 */
	public final int getZOff() {
		return zOff;
	}

	/**
	 * Gets the scalar value of how far the entity is to the next tile.
	 * 
	 * @return the scalar movement value
	 */
	public final float getMovementPercent() {
		return movementPercent;
	}

	/**
	 * Force starts the movement loop.
	 */
	public final void forceStartMoveLoop() {
		xOff = 0;
		zOff = 0;
		moveStart = System.currentTimeMillis();
	}
}
