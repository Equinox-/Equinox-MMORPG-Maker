package com.pi.common.game;

/**
 * Entity subclass representing an entity with a health bar.
 * 
 * @author Westin
 * 
 */
public class LivingEntity extends Entity {
	/**
	 * Entity's current health.
	 */
	private int health = 1;

	/**
	 * Sets an entity's current health.
	 * 
	 * @param sHealth the new health
	 */
	public final void setHealth(final int sHealth) {
		this.health = sHealth;
	}

	/**
	 * Gets the entity's current health.
	 * 
	 * @return the entity's health
	 */
	public final int getHealth() {
		return health;
	}
}
