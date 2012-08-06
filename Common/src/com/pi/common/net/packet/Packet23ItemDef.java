package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.database.def.ItemDef;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent from the server to the client to synchronize the server's item
 * definition with the client.
 * 
 * @author mark
 * 
 */
public class Packet23ItemDef extends Packet {
	public int itemID;
	public ItemDef def;

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(itemID);
		if (def == null) {
			def = new ItemDef(itemID);
		}
		def.writeData(pOut);

	}

	@Override
	public final int getLength() {
		if (def == null) {
			def = new ItemDef(itemID);
		}
		return SizeOf.INT + def.getLength();
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		itemID = pIn.readInt();
		if (def == null) {
			def = new ItemDef(itemID);
		}
		def.readData(pIn);
	}

}
