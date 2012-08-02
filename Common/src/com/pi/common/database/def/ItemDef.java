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
	
	// Other item attribs
	
	public ItemDef(final int iDefID) {
		this.defID = iDefID;
	}

	public int getDefID() {
		return defID;
	}

	public void setDefID(int defID) {
		this.defID = defID;
	}

	@Override
	public void writeData(PacketOutputStream pOut)
			throws IOException {
		super.writeData(pOut);
	}

	@Override
	public void readData(PacketInputStream pIn)
			throws IOException {
		super.readData(pIn);
	}

	@Override
	public int getLength() {
		return super.getLength();
	}
}
