package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.constants.ItemConstants;
import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.database.Item;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet containing information for an inventory update.
 * 
 * @author mark
 * 
 */
public class Packet25InventoryUpdate extends Packet {
	public Item updatedItem;
	public int inventoryID;

	/**
	 * Creates the packet with a given Item and inventory slot (ID).
	 * 
	 * @param updatedItem the new item
	 * @param inventoryID where it is in the inventory
	 * @return the packet instance
	 */
	public static Packet25InventoryUpdate create(
			final Item updatedItem, final int inventoryID) {
		Packet25InventoryUpdate p =
				new Packet25InventoryUpdate();
		p.updatedItem = updatedItem;
		p.inventoryID = inventoryID;
		return p;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		if (updatedItem == null) {
			updatedItem = ItemConstants.createNullItem();
		}
		pOut.writeInt(inventoryID);
		updatedItem.writeData(pOut);
	}

	@Override
	public final int getLength() {
		if (updatedItem == null) {
			updatedItem = ItemConstants.createNullItem();
		}
		return updatedItem.getLength() + SizeOf.INT;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		if (updatedItem == null) {
			updatedItem = ItemConstants.createNullItem();
		}
		inventoryID = pIn.readInt();
		updatedItem.readData(pIn);

	}

}
