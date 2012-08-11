package com.pi.common.game.entity;

/**
 * Entity subclass representing an entity that is actually an item on the map.
 * 
 * @author Westin
 * 
 */
public class ItemEntity extends Entity {
	/**
	 * This entity's item ID.
	 */
	private int item = -1;

	/**
	 * Sets an entity's current item.
	 * 
	 * @param sItem the new item
	 */
	public final void setItem(final int sItem) {
		this.item = sItem;
	}

	/**
	 * Gets the entity's current item.
	 * 
	 * @return the entity's item
	 */
	public final int getItem() {
		return item;
	}
}
