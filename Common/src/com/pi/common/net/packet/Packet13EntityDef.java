package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.def.EntityDef;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet13EntityDef extends Packet {
	public int entityID;
	public EntityDef def;

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeInt(entityID);
		if (def == null)
			def = new EntityDef();
		def.writeData(pOut);
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		entityID = pIn.readInt();
		if (def == null)
			def = new EntityDef();
		def.readData(pIn);
	}

	@Override
	public int getID() {
		return 13;
	}

	@Override
	public int getLength() {
		if (def == null)
			def = new EntityDef();
		return 4 + def.getLength();
	}
}
