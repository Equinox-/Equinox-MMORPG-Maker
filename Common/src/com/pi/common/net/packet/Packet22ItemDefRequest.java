package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet sent from the client to the server to request an item definition.
 * 
 * @author mark
 * 
 */
public class Packet22ItemDefRequest extends Packet {
	public int defID;

	/**
	 * Create an instance of the item definition request packet with the given
	 * definition identification number.
	 * 
	 * @param defID the definition id
	 * @return the packet instance
	 */
	public static Packet22ItemDefRequest create(final int defID) {
		Packet22ItemDefRequest r = new Packet22ItemDefRequest();
		r.defID = defID;
		return r;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(defID);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		defID = pIn.readInt();
	}

	@Override
	public final int getLength() {
		return SizeOf.INT;
	}
}
