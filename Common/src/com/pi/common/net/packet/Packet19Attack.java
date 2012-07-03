package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet representing a client pressing the attack button.
 * 
 * @author Westin
 * 
 */
public class Packet19Attack extends Packet {

	@Override
	public void writeData(final PacketOutputStream pOut)
			throws IOException {
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public void readData(final PacketInputStream pIn)
			throws IOException {
	}
}
