package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Inventory;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet containing information for an inventory.
 * 
 * @author mark
 * 
 */
public class Packet24InventoryData extends Packet {
	public Inventory inventory;

	/**
	 * Creates the packet with a given inventory.
	 * 
	 * @param inventory the inventory
	 * @return the packet instance
	 */
	public static Packet24InventoryData create(
			final Inventory inventory) {
		if (inventory == null) {
			throw new IllegalArgumentException(
					"Inventory can't be null!");
		}
		Packet24InventoryData p = new Packet24InventoryData();
		p.inventory = inventory;
		return p;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		inventory.writeData(pOut);
	}

	@Override
	public final int getLength() {
		if (inventory == null) {
			return 0;
		} else {
			return inventory.getLength();
		}
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		if (inventory == null) {
			inventory = new Inventory(0);
		}
		inventory.readData(pIn);
	}

}
