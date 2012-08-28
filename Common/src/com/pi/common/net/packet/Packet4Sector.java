package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.world.Sector;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent by the client to change a sector on the server, and by the
 * server to update the cached sectors on the client.
 * 
 * @author Westin
 * 
 */
public class Packet4Sector extends Packet {
	public Sector sector;

	@Override
	public final void writeData(final PacketOutputStream o)
			throws IOException {
		if (sector == null) {
			sector = new Sector();
		}
		sector.writeData(o);
	}

	@Override
	public final void readData(final PacketInputStream o)
			throws IOException {
		if (sector == null) {
			sector = new Sector();
		}
		sector.readData(o);
	}

	@Override
	public final int getLength() {
		if (sector == null) {
			sector = new Sector();
		}
		return sector.getLength();
	}
}
