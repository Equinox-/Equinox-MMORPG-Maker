package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.game.Inventory;
import com.pi.common.game.Item;
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
	 * @param updatedItem the new item
	 * @param inventoryID where it is in the inventory
	 * @return the packet instance
	 */
	public static Packet25InventoryUpdate create(
			final Item updatedItem, final int inventoryID) {
		Packet25InventoryUpdate p = new Packet25InventoryUpdate();
		p.updatedItem = updatedItem;
		p.inventoryID = inventoryID;
		return p;
	}

	@Override
	public void writeData(PacketOutputStream pOut)
			throws IOException {
		updatedItem.writeData(pOut);
		pOut.writeInt(inventoryID);
	}

	@Override
	public int getLength() {
		return updatedItem.getLength() + SizeOf.INT;
	}

	@Override
	public void readData(PacketInputStream pIn)
			throws IOException {
		updatedItem.readData(pIn);
		inventoryID = pIn.readInt();

	}

}
