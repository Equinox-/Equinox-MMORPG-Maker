package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.game.Inventory;
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
	 * Creates the packet with a given inventory
	 * 
	 * @param inventory the inventory
	 * @return the packet instance
	 */
	public static Packet24InventoryData create(
			final Inventory inventory) {
		Packet24InventoryData p = new Packet24InventoryData();
		p.inventory = inventory;
		return p;
	}

	@Override
	public void writeData(PacketOutputStream pOut)
			throws IOException {
		inventory.writeData(pOut);
	}

	@Override
	public int getLength() {
		return inventory.getLength();
	}

	@Override
	public void readData(PacketInputStream pIn)
			throws IOException {
		inventory.readData(pIn);
	}

}
