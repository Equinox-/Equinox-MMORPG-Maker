package com.pi.server.logic.entity;

import java.util.Random;

import com.pi.common.contants.Direction;
import com.pi.common.database.Location;
import com.pi.common.game.Entity;
import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

/**
 * Super class for any entity logic.
 * 
 * @author Westin
 * 
 */
public abstract class EntityLogic {
	/**
	 * The entity wrapper this logic instance is bound to.
	 */
	private final ServerEntity sEntity;
	/**
	 * The server this logic instance is bound to.
	 */
	private final Server server;

	/**
	 * The random instance bound to this logic instance.
	 */
	private final Random rand = new Random();

	/**
	 * Creates a logic instance of the given server entity and server.
	 * 
	 * @param sSEntity the entity wrapper
	 * @param sServer the server
	 */
	public EntityLogic(final ServerEntity sSEntity,
			final Server sServer) {
		this.server = sServer;
		this.sEntity = sSEntity;
	}

	/**
	 * Gets the server this logic instance is bound to.
	 * 
	 * @return the server instance
	 */
	protected final Server getServer() {
		return server;
	}

	/**
	 * Gets the entity this logic instance is bound to.
	 * 
	 * @return the entity
	 */
	protected final Entity getEntity() {
		return sEntity.getWrappedEntity();
	}

	/**
	 * Gets the random instance this logic is bound to.
	 * 
	 * @return the random instance
	 */
	protected final Random getRandom() {
		return rand;
	}

	/**
	 * Gets the entity wrapper this logic instance is bound to.
	 * 
	 * @return the server entity
	 */
	protected final ServerEntity getServerEntity() {
		return sEntity;
	}

	/**
	 * Try to move the entity in the given direction.
	 * 
	 * @param d the direction to move in
	 * @return if the entity was moved
	 */
	protected final boolean tryMove(final Direction d) {
		if (getEntity().canMoveIn(server.getWorld(), d)
				&& !sEntity.isStillMoving()) {
			getEntity().setDir(d);
			Location curr =
					new Location(getEntity().x,
							getEntity().plane, getEntity().z);
			sEntity.doTimedMovement();
			server.getEntityManager().sendEntityMove(
					getEntity().getEntityID(), curr,
					getEntity(), d);
			return true;
		}
		return false;
	}

	/**
	 * Does the logic for this logic instance.
	 */
	public abstract void doLogic();
}
