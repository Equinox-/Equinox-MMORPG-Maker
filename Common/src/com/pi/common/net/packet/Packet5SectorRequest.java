package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet sent by the client to the server to request a sector data update if
 * the revisions are different.
 * 
 * @author Westin
 * 
 */
public class Packet5SectorRequest extends Packet {
	public int baseX;
	public int plane;
	public int baseZ;
	public int revision;

	@Override
	public final void writeData(final PacketOutputStream dOut)
			throws IOException {
		dOut.writeInt(baseX);
		dOut.writeInt(plane);
		dOut.writeInt(baseZ);
		dOut.writeInt(revision);
	}

	@Override
	public final void readData(final PacketInputStream dIn)
			throws IOException {
		baseX = dIn.readInt();
		plane = dIn.readInt();
		baseZ = dIn.readInt();
		revision = dIn.readInt();
	}

	@Override
	public final int getLength() {
		return SizeOf.INT * 4;
	}
}
