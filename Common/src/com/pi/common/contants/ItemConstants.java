package com.pi.common.contants;

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
	public static final int PLAYER_INVENTORY_SIZE = 16;
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
