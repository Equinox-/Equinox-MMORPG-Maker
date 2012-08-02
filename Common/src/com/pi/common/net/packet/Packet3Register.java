package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet the client send to the server to register a new account.
 * 
 * @author Westin
 * 
 */
public class Packet3Register extends Packet {
	public String username;
	public String password;

	@Override
	public final void writeData(final PacketOutputStream dOut)
			throws IOException {
		dOut.writeString(username);
		dOut.writeString(password);
	}

	@Override
	public final void readData(final PacketInputStream dIn)
			throws IOException {
		username = dIn.readString();
		password = dIn.readString();
	}

	@Override
	public final int getLength() {
		return PacketOutputStream.stringByteLength(username)
				+ PacketOutputStream.stringByteLength(password);
	}

	@Override
	public final boolean requiresHandshake() {
		return true;
	}
}
