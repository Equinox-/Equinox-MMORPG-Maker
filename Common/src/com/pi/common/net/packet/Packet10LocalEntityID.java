package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet10LocalEntityID extends Packet {
    public int entityID;

    @Override
    protected void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entityID);
    }

    @Override
    protected void readData(PacketInputStream pIn) throws IOException {
	entityID = pIn.readInt();
    }

    public static Packet10LocalEntityID getPacket(int entity) {
	Packet10LocalEntityID pack = new Packet10LocalEntityID();
	pack.entityID = entity;
	return pack;
    }
}