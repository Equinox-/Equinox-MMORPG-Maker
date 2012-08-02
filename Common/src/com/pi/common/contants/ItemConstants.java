package com.pi.common.contants;

import com.pi.common.game.Item;

/**
 * Constants containing for items and the inventory
 * 
 * @author mark
 * 
 */
public final class ItemConstants {
	/**
	 * Inventory size
	 */
	public static final int inventorySize = 16;
	/**
	 * Stack size
	 */
	public static final int stackSize = 64;
	/**
	 * Null item
	 */
	public static final Item nullItem = new Item(-1, 0);

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private ItemConstants() {
	}
}
