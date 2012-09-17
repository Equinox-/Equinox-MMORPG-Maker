package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.constants.InteractionButton;
import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet representing a client pressing the attack button.
 * 
 * @author Westin
 * 
 */
public class Packet19Interact extends Packet {
	public InteractionButton button = InteractionButton.ATTACK;

	@Override
	public void writeData(final PacketOutputStream pOut) throws IOException {
		if (button != null) {
			pOut.writeByte(button.ordinal());
		} else {
			pOut.writeByte(0);
		}
	}

	@Override
	public int getLength() {
		return SizeOf.BYTE;
	}

	@Override
	public void readData(final PacketInputStream pIn) throws IOException {
		int index = pIn.readByte();
		if (index < 0 || index >= InteractionButton.values().length) {
			index = 0;
		}
		button = InteractionButton.values()[index];
	}

	public static Packet19Interact create(InteractionButton b) {
		Packet19Interact p = new Packet19Interact();
		p.button = b;
		return p;
	}
}
