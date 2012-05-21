package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet16EntityMove extends EntityMovementPacket {
	public int entity;

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeInt(entity);
		super.writeData(pOut);
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		entity = pIn.readInt();
		super.readData(pIn);
	}

	public static Packet16EntityMove create(int entity, Location l) {
		Packet16EntityMove pack = new Packet16EntityMove();
		pack.entity = entity;
		pack.xPart = l.x % DATA_MAX;
		pack.zPart = l.z % DATA_MAX;
		return pack;
	}

	@Override
	public int getID() {
		return 16;
	}

	@Override
	public int getLength() {
		return 4 + super.getLength();
	}
}
