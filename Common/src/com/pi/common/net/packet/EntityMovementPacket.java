package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public abstract class EntityMovementPacket extends Packet {
	private static final int DATA_SIZE_BITS = 8;

	private static final int DATA_HALF_MAX = (int) Math.pow(2,
			DATA_SIZE_BITS - 1);
	protected static final int DATA_MAX = DATA_HALF_MAX * 2;
	private static final int DATA_MASK = DATA_MAX - 1;

	protected int xPart, zPart;
	private long dat = 0;

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		dat = ((xPart << DATA_SIZE_BITS) & (DATA_MASK << DATA_SIZE_BITS))
				+ (zPart & DATA_MASK);
		switch (DATA_SIZE_BITS) {
		case 4:
			pOut.writeByte((byte) dat);
			break;
		case 8:
			pOut.writeShort((int) dat);
			break;
		case 16:
			pOut.writeInt((int) dat);
			break;
		case 32:
			pOut.writeLong(dat);
			break;
		default:
			throw new RuntimeException("DATA_SIZE_BITS is of incorrect size!");
		}
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		switch (DATA_SIZE_BITS) {
		case 4:
			dat = pIn.readByte();
			break;
		case 8:
			dat = pIn.readShort();
			break;
		case 16:
			dat = pIn.readInt();
			break;
		case 32:
			dat = pIn.readLong();
			break;
		default:
			throw new RuntimeException("DATA_SIZE_BITS is of incorrect size!");
		}
		xPart = (int) ((dat >> DATA_SIZE_BITS) & DATA_MASK);
		zPart = (int) (dat & DATA_MASK);
	}

	public Location apply(Location l) {
		int cXO = l.x % DATA_MAX;
		int cZO = l.z % DATA_MAX;
		int xOC = xPart - cXO;
		int zOC = zPart - cZO;
		int newX = l.x - cXO;
		int newZ = l.z - cZO;
		if (xOC > DATA_HALF_MAX) {
			newX += xPart - DATA_MAX;
		} else if (xOC < -DATA_HALF_MAX) {
			newX += DATA_MAX + xPart;
		} else {
			newX += xPart;
		}
		if (zOC > DATA_HALF_MAX) {
			newZ += zPart - DATA_MAX;
		} else if (zOC < -DATA_HALF_MAX) {
			newZ += DATA_MAX + zPart;
		} else {
			newZ += zPart;
		}
		return new Location(newX, l.getPlane(), newZ);
	}

	@Override
	public int getLength() {
		return DATA_SIZE_BITS / 4;
	}
}
