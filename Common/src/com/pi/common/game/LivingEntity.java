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
	private float health;

	/**
	 * Sets an entity's current health.
	 * 
	 * @param sHealth the new health
	 */
	public final void setHealth(final float sHealth) {
		this.health = sHealth;
	}

	/**
	 * Gets the entity's current health.
	 * 
	 * @return the entity's health
	 */
	public final float getHealth() {
		return health;
	}
}
