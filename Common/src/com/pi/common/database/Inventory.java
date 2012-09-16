package com.pi.common.database;

import java.io.IOException;
import java.util.Iterator;

import com.pi.common.contants.ItemConstants;
import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * Class for holding items.
 * 
 * @author mark
 * 
 */
public class Inventory implements PacketObject, Iterable<Item> {
	/**
	 * Array containing the player's inventory.
	 */
	private Item[] inventory;

	/**
	 * Creates a new inventory, with the given size.
	 * 
	 * @param size
	 *            the allocated size
	 */
	public Inventory(final int size) {
		inventory = new Item[size];
	}

	/**
	 * Gets the number of slots in this inventory.
	 * 
	 * @return the slot count
	 */
	public final int getSlotCount() {
		return inventory.length;
	}

	/**
	 * Get the item in the inventory at the current location (0 based).
	 * 
	 * More specifically this returns the inventory item at the given index if
	 * it not <code>null</code>, otherwise this method returns
	 * {@link ItemConstants#createNullItem()}.
	 * 
	 * @param inventoryID
	 *            the inventory location
	 * @return the item in the location
	 */
	public final Item getInventoryAt(final int inventoryID) {
		if (inventory[inventoryID] == null) {
			inventory[inventoryID] = ItemConstants.createNullItem();
		}
		return inventory[inventoryID];
	}

	/**
	 * Set the item in the inventory at the current location (0 based).
	 * 
	 * More specifically this method sets the inventory item at the given index
	 * to the given argument, unless the argument is null, in which case this
	 * method sets the inventory item to {@link ItemConstants#createNullItem()}.
	 * 
	 * @param inventoryID
	 *            the inventory location
	 * @param item
	 *            the item in the location
	 */
	public final void setInventoryAt(final int inventoryID, final Item item) {
		if (item == null) {
			inventory[inventoryID] = ItemConstants.createNullItem();
		} else {
			inventory[inventoryID] = item;
		}
	}

	/**
	 * Clears all items in the inventory.
	 * 
	 * More specifically this method sets every item in this inventory to
	 * {@link ItemConstants#createNullItem()}.
	 */
	public final void clear() {
		for (int i = 0; i < inventory.length; i++) {
			inventory[i] = ItemConstants.createNullItem();
		}
	}

	/**
	 * Resizes this inventory to the given new size, preserving the data.
	 * 
	 * @param newSize
	 *            the new size
	 */
	public final void resize(final int newSize) {
		Item[] temp = new Item[newSize];
		System.arraycopy(inventory, 0, temp, 0,
				Math.min(newSize, inventory.length));
		inventory = temp;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(inventory.length);
		for (int i = 0; i < inventory.length; i++) {
			getInventoryAt(i).writeData(pOut);
		}
	}

	@Override
	public final int getLength() {
		int size = SizeOf.INT;
		for (int i = 0; i < inventory.length; i++) {
			size += getInventoryAt(i).getLength();
		}
		return size;
	}

	@Override
	public final void readData(final PacketInputStream pIn) throws IOException {
		int length = pIn.readInt();
		if (inventory.length != length) {
			inventory = new Item[length];
		}
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] == null) {
				inventory[i] = ItemConstants.createNullItem();
			}
			inventory[i].readData(pIn);
		}
	}

	@Override
	public final Iterator<Item> iterator() {
		return new Iterator<Item>() {
			private int currentIndex = -1;
			private Item rNext = getNext();

			@Override
			public boolean hasNext() {
				return rNext != null;
			}

			@Override
			public Item next() {
				Item temp = rNext;
				rNext = getNext();
				return temp;
			}

			/**
			 * Gets the next non-empty item in the inventory.
			 * 
			 * @return the next item
			 */
			private Item getNext() {
				for (int i = currentIndex + 1; i < inventory.length; i++) {
					Item itm = getInventoryAt(i);
					if (itm != null && itm.getItemID() >= 0
							&& itm.getItemCount() > 0) {
						currentIndex = i;
						return itm;
					}
				}
				return null;
			}

			@Override
			public void remove() {
				if (currentIndex >= 0 && currentIndex < inventory.length) {
					setInventoryAt(currentIndex, null);
				}
				rNext = getNext();
			}
		};
	}

	public int getFreeSlot() {
		for (int slot = 0; slot < inventory.length; slot++) {
			if (inventory[slot] == null || inventory[slot].getItemID() <= 0
					|| inventory[slot].getItemCount() <= 0) {
				return slot;
			}
		}
		return -1;
	}
}
