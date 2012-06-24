package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet12EntityDefRequest extends Packet {
	public int defID;

	public static Packet12EntityDefRequest create(int defID) {
		Packet12EntityDefRequest r =
				new Packet12EntityDefRequest();
		r.defID = defID;
		return r;
	}

	@Override
	public void writeData(PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(defID);
	}

	@Override
	public void readData(PacketInputStream pIn)
			throws IOException {
		defID = pIn.readInt();
	}

	@Override
	public int getLength() {
		return 4;
	}
}
