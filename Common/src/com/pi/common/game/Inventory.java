package com.pi.common.game;

import java.io.IOException;

import com.pi.common.contants.ItemConstants;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * Class for holding items.
 * 
 * @author mark
 * 
 */
public class Inventory implements PacketObject {
	int size = ItemConstants.inventorySize;
	/**
	 * Array containing the player's inventory
	 */
	private Item[] inventory = new Item[size];

	/**
	 * Get the item in the inventory at the current location (0 based)
	 * 
	 * @param inventoryID the inventory location
	 * @return the item in the location
	 */
	public final Item getInventoryAt(int inventoryID) {
		if (inventory[inventoryID] == null) {
			inventory[inventoryID] = ItemConstants.nullItem;
		}
		return inventory[inventoryID];
	}

	/**
	 * Set the item in the inventory at the current location (0 based)
	 * 
	 * @param inventoryID the inventory location
	 * @param item the item in the location
	 */
	public final void setInventoryAt(int inventoryID, Item item) {
		inventory[inventoryID] = item;
	}

	@Override
	public void writeData(PacketOutputStream pOut)
			throws IOException {
		for (Item item:inventory) {
			item.writeData(pOut);
		}
	}

	@Override
	public int getLength() {
		// Janky
		return size * getInventoryAt(0).getLength();
	}

	@Override
	public void readData(PacketInputStream pIn)
			throws IOException {
		for (Item item:inventory) {
			item.readData(pIn);
		}
	}

}
