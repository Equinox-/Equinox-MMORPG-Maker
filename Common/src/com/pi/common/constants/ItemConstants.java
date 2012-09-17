package com.pi.common.constants;

import com.pi.common.database.Item;

/**
 * Constants containing for items and the inventory.
 * 
 * @author mark
 * 
 */
public final class ItemConstants {
	/**
	 * The default inventory size for a player.
	 */
	public static final int PLAYER_INVENTORY_SIZE = 24;
	/**
	 * The default amount of time in milliseconds for an entity to pickup an
	 * item.
	 */
	public static final long DEFAULT_ITEM_PICKUP_SPEED = 1000;
	/**
	 * The maximum stack size.
	 */
	public static final int MAX_STACK_SIZE = 64;

	/**
	 * Creates a null, or empty item.
	 * 
	 * @return a null, or empty item
	 */
	public static Item createNullItem() {
		return new Item(-1, 0);
	}

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private ItemConstants() {
	}
}
