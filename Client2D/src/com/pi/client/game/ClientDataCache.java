package com.pi.client.game;

import com.pi.common.constants.ItemConstants;
import com.pi.common.database.Inventory;

/**
 * A simple class that contains cached data for the client.
 * 
 * @author Westin
 * 
 */
public class ClientDataCache {
	/**
	 * The cached inventory data.
	 */
	private Inventory inventoryCache = new Inventory(
			ItemConstants.PLAYER_INVENTORY_SIZE);

	/**
	 * Gets this cache's inventory.
	 * 
	 * @return the cached inventory
	 */
	public final Inventory getInventory() {
		return inventoryCache;
	}

	/**
	 * Sets this cache's inventory to a new inventory instance.
	 * 
	 * @param i the new inventory instance
	 */
	public final void setInventory(final Inventory i) {
		this.inventoryCache = i;
	}
}
