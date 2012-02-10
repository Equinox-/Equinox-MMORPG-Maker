package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Sector;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet4Sector extends Packet {
    public Sector sector;

    @Override
    protected void writeData(PacketOutputStream o) throws IOException {
	sector.write(o);
    }

    @Override
    protected void readData(PacketInputStream o) throws IOException {
	if (sector == null)
	    sector = new Sector();
	sector.read(o);
    }

    @Override
    public int getID() {
	return 4;
    }

    @Override
    public int getLength() {
	return sector.getLength();
    }
}
