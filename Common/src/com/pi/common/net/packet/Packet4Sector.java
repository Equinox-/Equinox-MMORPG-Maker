package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.Sector;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet4Sector extends Packet {
    public Sector sector;

    @Override
    public void writeData(PacketOutputStream o) throws IOException {
	if (sector == null)
	    sector = new Sector();
	sector.writeData(o);
    }

    @Override
    public void readData(PacketInputStream o) throws IOException {
	if (sector == null)
	    sector = new Sector();
	sector.readData(o);
    }

    @Override
    public int getID() {
	return 4;
    }

    @Override
    public int getLength() {
	if (sector == null)
	    sector = new Sector();
	return sector.getLength();
    }
}
