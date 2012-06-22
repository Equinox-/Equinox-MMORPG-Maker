package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet17Clock extends Packet {
	public long clientSendTime;
	public long serverSendTime;

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeLong(clientSendTime);
		pOut.writeLong(serverSendTime);
	}

	@Override
	public int getLength() {
		return 16;
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		clientSendTime = pIn.readLong();
		serverSendTime = pIn.readLong();
	}

}
