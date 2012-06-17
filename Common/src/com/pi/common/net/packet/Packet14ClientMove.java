package com.pi.common.net.packet;

import com.pi.common.database.Location;

public class Packet14ClientMove extends EntityMovementPacket {
	public static Packet14ClientMove create(Location l) {
		Packet14ClientMove pack = new Packet14ClientMove();
		pack.xPart = l.x % DATA_MAX;
		pack.zPart = l.z % DATA_MAX;
		return pack;
	}

	@Override
	public int getLength() {
		return super.getLength();
	}
}
