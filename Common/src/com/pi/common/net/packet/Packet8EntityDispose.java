package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet8EntityDispose extends Packet {
    public int entityID;

    public static Packet8EntityDispose create(int id) {
	Packet8EntityDispose p = new Packet8EntityDispose();
	p.entityID = id;
	return p;
    }

    @Override
    protected void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entityID);
    }

    @Override
    protected void readData(PacketInputStream pIn) throws IOException {
	entityID = pIn.readInt();
    }
    @Override
    public int getID() {
	return 8;
    }
}
