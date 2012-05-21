package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet2Alert extends Packet {
	public static Packet2Alert create(String message) {
		Packet2Alert p = new Packet2Alert();
		p.message = message;
		return p;
	}

	public String message;

	@Override
	public void writeData(PacketOutputStream dOut) throws IOException {
		dOut.writeString(message);
	}

	@Override
	public void readData(PacketInputStream dIn) throws IOException {
		message = dIn.readString();
	}

	@Override
	public int getID() {
		return 2;
	}

	@Override
	public int getLength() {
		return PacketOutputStream.stringByteLength(message);
	}
}
