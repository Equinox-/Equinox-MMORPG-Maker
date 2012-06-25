package com.pi.common.contants;

/**
 * A class containing tile flag constants.
 * 
 * @author Westin
 * 
 */
public final class TileFlags {
	/**
	 * The flag for a northern directional block.
	 */
	public static final int WALL_NORTH = 1;
	/**
	 * The flag for a southern directional block.
	 */
	public static final int WALL_SOUTH = 2;
	/**
	 * The flag for a eastern directional block.
	 */
	public static final int WALL_EAST = 4;
	/**
	 * The flag for a western directional block.
	 */
	public static final int WALL_WEST = 8;
	/**
	 * The flag for an all directions block.
	 */
	public static final int BLOCKED = WALL_NORTH | WALL_SOUTH
			| WALL_EAST | WALL_WEST;

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private TileFlags() {
	}
}
