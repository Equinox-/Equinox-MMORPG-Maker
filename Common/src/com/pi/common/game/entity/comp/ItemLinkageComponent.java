package com.pi.common.game.entity.comp;

import java.io.IOException;

import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * An entity component that provides a linkage between an item definition and an
 * entity, to allow items dropped on the ground to use the entity system.
 * 
 * @author westin
 * 
 */
public class ItemLinkageComponent extends EntityComponent {
	/**
	 * The item definition represented by this linkage.
	 */
	private int itemDefID;

	/**
	 * Creates an empty component.
	 */
	public ItemLinkageComponent() {
	}

	/**
	 * Creates an item linkage component for the given item definition.
	 * 
	 * @param sItem the item definition
	 */
	public ItemLinkageComponent(final int sItem) {
		this.itemDefID = sItem;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(itemDefID);
	}

	@Override
	public final int getLength() {
		return SizeOf.INT;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		itemDefID = pIn.readInt();
	}

	/**
	 * Gets the item definition that this linkage component links with.
	 * 
	 * @return the item definition ID
	 */
	public final int getItemID() {
		return itemDefID;
	}

}
