package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent by the server to the client to request that the login
 * credentials be verified and that the client should be put into the main game
 * state if verification is successful.
 * 
 * @author Westin
 * 
 */
public class Packet1Login extends Packet {
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
