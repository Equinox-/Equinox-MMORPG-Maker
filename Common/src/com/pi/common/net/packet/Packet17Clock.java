package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A class representing the clock packet used to synchronize the client time
 * with the server and get the ping. This is sent by both the server and client.
 * 
 * @author Westin
 * 
 */
public class Packet17Clock extends Packet {
	public long clientSendTime;
	public long serverSendTime;

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeLong(clientSendTime);
		pOut.writeLong(serverSendTime);
	}

	@Override
	public final int getLength() {
		return SizeOf.LONG * 2;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		clientSendTime = pIn.readLong();
		serverSendTime = pIn.readLong();
	}

}
