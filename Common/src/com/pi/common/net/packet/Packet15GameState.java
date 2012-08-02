package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.game.GameState;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet sent by the server to the client to change the game state.
 * 
 * @author Westin
 * 
 */
public class Packet15GameState extends Packet {
	public GameState state;

	/**
	 * Creates a new instance of this packet with the specified game state.
	 * 
	 * @param state the specified state
	 * @return the packet instance
	 */
	public static Packet15GameState create(final GameState state) {
		Packet15GameState pack = new Packet15GameState();
		pack.state = state;
		return pack;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		if (state != null) {
			pOut.writeInt(state.ordinal());
		} else {
			pOut.writeInt(-1);
		}
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		int idx = pIn.readInt();
		if (idx >= 0 && idx < GameState.values().length) {
			state = GameState.values()[idx];
		} else {
			state = null;
		}
	}

	@Override
	public final int getLength() {
		return SizeOf.INT;
	}

	@Override
	public final boolean requiresHandshake() {
		return true;
	}
}
