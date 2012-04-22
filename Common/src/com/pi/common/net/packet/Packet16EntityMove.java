package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet16EntityMove extends Packet {
    public int entity;
    private int xPart, zPart;
    byte dat;

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entity);
	pOut.writeByte(((xPart << 4) & 240) + (zPart & 15));
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	entity = pIn.readInt();
	dat = pIn.readByte();
	xPart = (dat >> 4) & 15;
	zPart = dat & 15;
    }

    public static Packet16EntityMove create(int entity, Location l) {
	Packet16EntityMove pack = new Packet16EntityMove();
	pack.entity = entity;
	pack.xPart = l.x % 16;
	pack.zPart = l.z % 16;
	return pack;
    }

    public Location apply(Location l) {
	int cXO = l.x % 16;
	int cZO = l.z % 16;
	int xOC = xPart - cXO;
	int zOC = zPart - cZO;
	int newX = l.x - cXO;
	int newZ = l.z - cZO;
	if (xOC > 8) {
	    newX += xPart - 16;
	} else if (xOC < -8) {
	    newX += 16 + xPart;
	} else {
	    newX += xPart;
	}
	if (zOC > 8) {
	    newZ += zPart - 16;
	} else if (zOC < -8) {
	    newZ += 16 + zPart;
	} else {
	    newZ += zPart;
	}
	return new Location(newX, l.getPlane(), newZ);
    }

    @Override
    public int getID() {
	return 16;
    }

    @Override
    public int getLength() {
	return 5;
    }
}
