package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet11LocalEntityID extends Packet {
	public int entityID;

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeInt(entityID);
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		entityID = pIn.readInt();
	}

	public static Packet11LocalEntityID getPacket(int entity) {
		Packet11LocalEntityID pack = new Packet11LocalEntityID();
		pack.entityID = entity;
		return pack;
	}

	@Override
	public int getLength() {
		return 4;
	}
}
