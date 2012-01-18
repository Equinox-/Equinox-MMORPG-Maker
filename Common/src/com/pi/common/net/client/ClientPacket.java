package com.pi.common.net.client;

import com.pi.common.net.packet.Packet;

public class ClientPacket {
	private final NetClient client;
	private final Packet packet;

	public ClientPacket(NetClient client, Packet packet) {
		this.packet = packet;
		this.client = client;
	}

	public void process() {
		if (client != null && client.isConnected() && !client.isQuitting())
			client.processPacket(packet);
	}
}
