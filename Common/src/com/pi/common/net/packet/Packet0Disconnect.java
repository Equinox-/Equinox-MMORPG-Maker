package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet0Disconnect extends Packet {
	public String reason;
	public String details;

	public Packet0Disconnect() {

	}

	public Packet0Disconnect(String reason, String details) {
		this.reason = reason;
		this.details = details;
	}

	@Override
	public void writeData(PacketOutputStream dOut) throws IOException {
		dOut.writeString(reason);
		dOut.writeString(details);
	}

	@Override
	public void readData(PacketInputStream dIn) throws IOException {
		reason = dIn.readString();
		details = dIn.readString();
	}

	@Override
	public int getID() {
		return 0;
	}

	@Override
	public int getLength() {
		return PacketOutputStream.stringByteLength(reason)
				+ PacketOutputStream.stringByteLength(details);
	}
}
