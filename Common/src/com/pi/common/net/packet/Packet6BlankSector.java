package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A packet sent by the server to the client to flag a sector as blank. Also
 * sent by the client to the server to delete a sector.
 * 
 * @author Westin
 * 
 */
public class Packet6BlankSector extends Packet {
	public int baseX;
	public int baseY;
	public int baseZ;

	@Override
	public final void writeData(final PacketOutputStream dOut)
			throws IOException {
		dOut.writeInt(baseX);
		dOut.writeInt(baseY);
		dOut.writeInt(baseZ);
	}

	@Override
	public final void readData(final PacketInputStream dIn)
			throws IOException {
		baseX = dIn.readInt();
		baseY = dIn.readInt();
		baseZ = dIn.readInt();
	}

	@Override
	public final int getLength() {
		return SizeOf.INT * 3;
	}
}
