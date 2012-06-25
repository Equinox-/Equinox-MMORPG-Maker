package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A class that represents a short entity movement in the minimum packet size.
 * 
 * @author Westin
 * 
 */
public abstract class EntityMovementPacket extends Packet {
	/**
	 * The number of bits used for each coordinate. Must be a power of two
	 * greater than 4.
	 */
	private static final int DATA_SIZE_BITS = 8;
	/**
	 * Half the maximum transmitted coordinate size.
	 */
	private static final int DATA_HALF_MAX = (int) Math.pow(2,
			DATA_SIZE_BITS - 1);
	/**
	 * The maximum transmitted coordinate size.
	 */
	private static final int DATA_MAX = DATA_HALF_MAX * 2;
	/**
	 * The mask applied to each coordinate.
	 */
	private static final int DATA_MASK = DATA_MAX - 1;

	/**
	 * The x coordinate.
	 */
	private int xPart;
	/**
	 * The z coordinate.
	 */
	private int zPart;
	/**
	 * The packed data.
	 */
	private long dat = 0;

	/**
	 * Sets the coordinate parts based on the world x and z position.
	 * 
	 * @param x the world x coordinate
	 * @param z the world z coordinate
	 */
	protected final void setCoordinateParts(int x, int z) {
		xPart = x % DATA_MAX;
		zPart = z % DATA_MAX;
	}

	@Override
	public void writeData(final PacketOutputStream pOut)
			throws IOException {
		dat =
				((xPart << DATA_SIZE_BITS) & (DATA_MASK << DATA_SIZE_BITS))
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
			throw new RuntimeException(
					"DATA_SIZE_BITS is of incorrect size!");
		}
	}

	@Override
	public void readData(final PacketInputStream pIn)
			throws IOException {
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
			throw new RuntimeException(
					"DATA_SIZE_BITS is of incorrect size!");
		}
		xPart = (int) ((dat >> DATA_SIZE_BITS) & DATA_MASK);
		zPart = (int) (dat & DATA_MASK);
	}

	/**
	 * Applies this movement packet to the given location.
	 * 
	 * @param l the current location
	 * @return the modified location
	 */
	public final Location apply(final Location l) {
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
