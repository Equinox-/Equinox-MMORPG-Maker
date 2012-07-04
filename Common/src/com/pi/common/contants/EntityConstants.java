package com.pi.common.contants;

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
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private EntityConstants() {
	}
}
