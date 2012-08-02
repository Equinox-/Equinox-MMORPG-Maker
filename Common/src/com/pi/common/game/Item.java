package com.pi.common.game;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * Item description class, for describing an item.
 * 
 * @author mark
 * 
 */

public class Item implements PacketObject {
	/**
	 * The item identification number currently in use.
	 */
	private int itemID;
	/**
	 * The amount of this item that there currently is.
	 */
	private int itemCount;

	// private final ItemDefinition itemDefinition;

	/**
	 * Creates an item and sets it's ID and count
	 * 
	 * @param itemID the item's ID
	 * @param itemCount the amount of the item
	 */
	public Item(int itemID, int itemCount) {
		this.itemID = itemID;
		this.itemCount = itemCount;
		// this.itemDefinition = ItemDefiner.getDefinition(itemID);
	}

	/**
	 * Get the current amount of the item
	 * 
	 * @return the amount of the item
	 */
	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int count) {
		itemCount = count;
	}

	/**
	 * Get the item's ID
	 * 
	 * @return the item's ID
	 */
	public int getItemID() {
		return itemID;
	}

	@Override
	public void writeData(PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(itemID);
		pOut.writeInt(itemCount);
	}

	@Override
	public int getLength() {
		return 2 * SizeOf.INT;
	}

	@Override
	public void readData(PacketInputStream pIn)
			throws IOException {
		itemID = pIn.readInt();
		itemCount = pIn.readInt();
	}
}
