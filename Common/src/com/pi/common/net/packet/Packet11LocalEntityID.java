package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet11LocalEntityID extends Packet {
    public int entityID;

    @Override
    protected void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entityID);
    }

    @Override
    protected void readData(PacketInputStream pIn) throws IOException {
	entityID = pIn.readInt();
    }

    public static Packet11LocalEntityID getPacket(int entity) {
	Packet11LocalEntityID pack = new Packet11LocalEntityID();
	pack.entityID = entity;
	return pack;
    }
    @Override
    public int getID() {
	return 11;
    }
}
