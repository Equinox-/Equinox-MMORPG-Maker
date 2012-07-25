package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * An alert packet that sends a message to the client for display graphically.
 * 
 * @author Westin
 * 
 */
public class Packet2Alert extends Packet {
	public String message;

	/**
	 * Creates an alert packet instance with the given message.
	 * 
	 * @param message the alert message
	 * @return the packet instance
	 */
	public static Packet2Alert create(final String message) {
		Packet2Alert p = new Packet2Alert();
		p.message = message;
		return p;
	}

	@Override
	public final void writeData(final PacketOutputStream dOut)
			throws IOException {
		dOut.writeString(message);
	}

	@Override
	public final void readData(final PacketInputStream dIn)
			throws IOException {
		message = dIn.readString();
	}

	@Override
	public final int getLength() {
		return PacketOutputStream.stringByteLength(message);
	}

	@Override
	public final boolean requiresHandshake() {
		return true;
	}
}
