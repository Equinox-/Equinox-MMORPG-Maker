package com.pi.common.database.def;

import java.io.IOException;

import com.pi.common.database.GraphicsObject;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * The class representing an item's definition.
 * 
 * @author mark
 * 
 */
public class ItemDef extends GraphicsObject {
	/**
	 * This definintion's identification number.
	 */
	private int defID = -1;

	/**
	 * Creates an item definitions with the given definition ID.
	 * 
	 * @param iDefID the definition ID
	 */
	public ItemDef(final int iDefID) {
		this.defID = iDefID;
	}

	/**
	 * Get's this definition's ID.
	 * 
	 * @return the definition ID
	 */
	public final int getDefID() {
		return defID;
	}

	/**
	 * Sets the definition ID of this item definition.
	 * 
	 * @param sDefID the definition ID
	 */
	public final void setDefID(final int sDefID) {
		this.defID = sDefID;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		super.writeData(pOut);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		super.readData(pIn);
	}

	@Override
	public final int getLength() {
		return super.getLength();
	}
}
