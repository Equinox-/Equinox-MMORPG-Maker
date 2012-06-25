package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet that the server sends to the client to kick clients from the server
 * with a given reason and details.
 * 
 * @author Westin
 * 
 */
public class Packet0Disconnect extends Packet {
	/**
	 * The kick reason.
	 */
	public String reason;
	/**
	 * Details for the kick.
	 */
	public String details;

	/**
	 * Creates an instance of the disconnect packet with the specified reason
	 * and details.
	 * 
	 * @param reason the reason this client was kicked
	 * @param details the details behind the kicking
	 * @return the packet instance
	 */
	public static Packet0Disconnect create(final String reason,
			final String details) {
		Packet0Disconnect pack = new Packet0Disconnect();
		pack.reason = reason;
		pack.details = details;
		return pack;
	}

	@Override
	public final void writeData(final PacketOutputStream dOut)
			throws IOException {
		dOut.writeString(reason);
		dOut.writeString(details);
	}

	@Override
	public final void readData(final PacketInputStream dIn)
			throws IOException {
		reason = dIn.readString();
		details = dIn.readString();
	}

	@Override
	public final int getLength() {
		return PacketOutputStream.stringByteLength(reason)
				+ PacketOutputStream.stringByteLength(details);
	}
}
