package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Location;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet14ClientMove extends Packet {
    public Location to;

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	if (to == null)
	    to = new Location();
	to.writeData(pOut);
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	if (to == null)
	    to = new Location();
	to.readData(pIn);
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
	if (to == null)
	    to = new Location();
	return to.getLength();
    }
}
