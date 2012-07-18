package com.pi.server.entity;

import com.pi.common.contants.EntityConstants;
import com.pi.common.game.Entity;
import com.pi.server.constants.ServerConstants;
import com.pi.server.logic.entity.EntityLogic;

/**
 * An entity container class that manages special variables for server entities.
 * 
 * @author Westin
 * 
 */
public class ServerEntity {
	/**
	 * The time in milliseconds that the next entity movement will be.
	 */
	private long nextMove = -1;
	/**
	 * The logic class instance.
	 */
	private EntityLogic logicClass;
	/**
	 * The entity this container wraps.
	 */
	private Entity wrap;

	/**
	 * The last entity to attack this entity.
	 */
	private int lastAttacker;
	/**
	 * The last time this entity was attacked.
	 */
	private long lastAttackerTime;

	/**
	 * Creates an entity container around the given entity.
	 * 
	 * @param wrapped the entity to wrap
	 */
	public ServerEntity(final Entity wrapped) {
		this.wrap = wrapped;
	}

	/**
	 * Checks if this entity is still moving.
	 * 
	 * @return if this entity is still moving
	 */
	public final boolean isStillMoving() {
		return nextMove > System.currentTimeMillis();
	}

	/**
	 * Checks if this entity is not still moving and if it is not, it moves it.
	 */
	public final void doTimedMovement() {
		if (!isStillMoving()) {
			nextMove =
					System.currentTimeMillis()
							+ EntityConstants.WALK_TIME;
			wrap.doMovement();
		}
	}

	/**
	 * Gets the logic instance bound to this server entity.
	 * 
	 * @return the entity logic instance
	 */
	public final EntityLogic getLogic() {
		return logicClass;
	}

	/**
	 * Assigns the given entity logic instance to this server entity if not
	 * already assigned.
	 * 
	 * @param l the logic instance to assign
	 * @return <code>true</code> if the logic instance was assigned,
	 *         <code>false</code> if not
	 */
	public final boolean assignLogic(final EntityLogic l) {
		if (logicClass == null) {
			logicClass = l;
			return true;
		}
		return false;
	}

	/**
	 * Gets the entity that this container wraps.
	 * 
	 * @return the wrapped entity
	 */
	public final Entity getWrappedEntity() {
		return wrap;
	}

	/**
	 * Sets the last entity to attack this entity, and updates the last attack
	 * time.
	 * 
	 * @param attacker the attacking entity
	 */
	public final void setAttacker(final int attacker) {
		this.lastAttacker = attacker;
		this.lastAttackerTime = System.currentTimeMillis();
	}

	/**
	 * Gets the last entity to attack this entity, or <code>-1</code> if there
	 * isn't a recent attacker.
	 * 
	 * More specifically, this will return <code>-1</code> if it was never
	 * attacked, or if the last attacker has expired, according to {@link
	 * ServerConstants#ENTITY_ATTACKER_TOLERANCE}.
	 * 
	 * @return the attacker
	 */
	public final int getAttacker() {
		if (lastAttackerTime
				+ ServerConstants.ENTITY_ATTACKER_TOLERANCE >= System
					.currentTimeMillis()) {
			return lastAttacker;
		} else {
			return -1;
		}
	}
}
