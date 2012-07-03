package com.pi.server.logic.entity;

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
	protected final ServerEntity sEntity;
	/**
	 * The actual entity this logic instance is bound to.
	 */
	protected final Entity entity;
	/**
	 * The server this logic instance is bound to.
	 */
	protected final Server server;

	/**
	 * Creates a logic instance of the given server entity and server.
	 * 
	 * @param sEntity the entity wrapper
	 * @param sServer the server
	 */
	public EntityLogic(final ServerEntity sEntity,
			final Server sServer) {
		this.server = sServer;
		this.sEntity = sEntity;
		this.entity = sEntity.getWrappedEntity();
	}

	/**
	 * Try to move the entity in the given direction.
	 * 
	 * @param d the direction to move in
	 * @return if the entity was moved
	 */
	protected final boolean tryMove(final Direction d) {
		if (entity.canMoveIn(server.getWorld(), d)
				&& !sEntity.isStillMoving()) {
			entity.setDir(d);
			Location curr =
					new Location(entity.x, entity.plane,
							entity.z);
			sEntity.doTimedMovement();
			server.getServerEntityManager().sendEntityMove(
					entity.getEntityID(), curr, entity, d);
			return true;
		}
		return false;
	}

	/**
	 * Does the logic for this logic instance.
	 */
	public abstract void doLogic();
}
