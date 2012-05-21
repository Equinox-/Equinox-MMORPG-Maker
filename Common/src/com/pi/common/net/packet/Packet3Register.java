package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet3Register extends Packet {
	public String username;
	public String password;

	@Override
	public void writeData(PacketOutputStream dOut) throws IOException {
		dOut.writeString(username);
		dOut.writeString(password);
	}

	@Override
	public void readData(PacketInputStream dIn) throws IOException {
		username = dIn.readString();
		password = dIn.readString();
	}

	@Override
	public int getID() {
		return 3;
	}

	@Override
	public int getLength() {
		return PacketOutputStream.stringByteLength(username)
				+ PacketOutputStream.stringByteLength(password);
	}
}
