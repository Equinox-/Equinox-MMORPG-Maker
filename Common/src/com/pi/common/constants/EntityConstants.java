package com.pi.common.constants;

/**
 * Class containing movement constants.
 * 
 * @author Westin
 * 
 */
public final class EntityConstants {
	/**
	 * The time in milliseconds to walk between tiles.
	 */
	public static final long WALK_TIME = 500;
	/**
	 * The time in milliseconds to run between tiles.
	 */
	public static final long RUN_TIME = 250;

	/**
	 * The default number of maximum health points for a living entity.
	 */
	public static final int DEFAULT_MAXIMUM_HEALTH = 10;

	/**
	 * The default amount of time in milliseconds for an entity to attack
	 * something.
	 */
	public static final long DEFAULT_ENTITY_ATTACK_SPEED = 750;

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private EntityConstants() {
	}
}
