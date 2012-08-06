package com.pi.common.database;

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

	/**
	 * Creates an item and sets it's ID and count.
	 * 
	 * @param sItemID the item's ID
	 * @param sItemCount the amount of the item
	 */
	public Item(final int sItemID, final int sItemCount) {
		this.itemID = sItemID;
		this.itemCount = sItemCount;
	}

	/**
	 * Get the current amount of the item.
	 * 
	 * @return the amount of the item
	 */
	public final int getItemCount() {
		return itemCount;
	}

	/**
	 * Sets the stack count of this item.
	 * 
	 * @param count the new stack size
	 */
	public final void setItemCount(final int count) {
		itemCount = count;
	}

	/**
	 * Get the item's ID.
	 * 
	 * @return the item's ID
	 */
	public final int getItemID() {
		return itemID;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(itemID);
		pOut.writeInt(itemCount);
	}

	@Override
	public final int getLength() {
		return 2 * SizeOf.INT;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		itemID = pIn.readInt();
		itemCount = pIn.readInt();
		if (itemCount == 0) {
			itemID = -1;
		}
	}
}
