package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet sent from the client to the server to request an entity definition.
 * 
 * @author Westin
 * 
 */
public class Packet12EntityDefRequest extends Packet {
	public int defID;

	/**
	 * Create an instance of the entity definition request packet with the given
	 * definition identification number.
	 * 
	 * @param defID the definition id
	 * @return the packet instance
	 */
	public static Packet12EntityDefRequest create(final int defID) {
		Packet12EntityDefRequest r =
				new Packet12EntityDefRequest();
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
