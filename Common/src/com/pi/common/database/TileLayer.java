package com.pi.common.database;

/**
 * An enum representing individual tile layers.
 * 
 * @author Westin
 * 
 */
public enum TileLayer {
	/**
	 * The base layer.
	 */
	GROUND,
	/**
	 * The mask is above the ground, but below the default entity layer.
	 */
	MASK1,
	/**
	 * Fringe is above the default entity layer.
	 */
	FRINGE1,
	/**
	 * The maximum layer. This is unused as a tile layer, but rather as a limit.
	 */
	MAX_VALUE
}