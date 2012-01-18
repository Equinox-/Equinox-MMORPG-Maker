package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.game.GameState;
import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet15GameState extends Packet {
	public GameState state;

	public static Packet15GameState create(GameState state) {
		Packet15GameState pack = new Packet15GameState();
		pack.state = state;
		return pack;
	}

	@Override
	public int getID() {
		return 15;
	}

	@Override
	protected void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeInt(state != null ? state.ordinal() : -1);
	}

	@Override
	protected void readData(PacketInputStream pIn) throws IOException {
		int idx = pIn.readInt();
		state = idx >= 0 && idx < GameState.values().length ? GameState
				.values()[idx] : null;
	}

}