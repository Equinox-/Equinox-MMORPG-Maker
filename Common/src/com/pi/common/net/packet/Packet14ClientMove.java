package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet14ClientMove extends Packet {
    public Location to;

    @Override
    protected void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(to.x);
	pOut.writeInt(to.plane);
	pOut.writeInt(to.z);
    }

    @Override
    protected void readData(PacketInputStream pIn) throws IOException {
	to = new Location(pIn.readInt(), pIn.readInt(), pIn.readInt());
    }

    public static Packet14ClientMove create(Location nL) {
	Packet14ClientMove pack = new Packet14ClientMove();
	pack.to = nL;
	return pack;
    }
    @Override
    public int getID() {
	return 14;
    }

    @Override
    public int getLength() {
	return 12;
    }
}
