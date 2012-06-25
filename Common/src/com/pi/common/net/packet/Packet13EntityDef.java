package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.database.def.EntityDef;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent from the server to the client to synchronize the server's
 * entity definintion with the client.
 * 
 * @author Westin
 * 
 */
public class Packet13EntityDef extends Packet {
	public int entityID;
	public EntityDef def;

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entityID);
		if (def == null) {
			def = new EntityDef();
		}
		def.writeData(pOut);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		entityID = pIn.readInt();
		if (def == null) {
			def = new EntityDef();
		}
		def.readData(pIn);
	}

	@Override
	public final int getLength() {
		if (def == null) {
			def = new EntityDef();
		}
		return SizeOf.INT + def.getLength();
	}
}
