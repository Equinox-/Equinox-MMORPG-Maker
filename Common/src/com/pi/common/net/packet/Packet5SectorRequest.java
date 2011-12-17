package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet5SectorRequest extends Packet {
    public int baseX;
    public int baseY;
    public int baseZ;
    public int revision;

    @Override
    protected void writeData(PacketOutputStream dOut) throws IOException {
	dOut.writeInt(baseX);
	dOut.writeInt(baseY);
	dOut.writeInt(baseZ);
	dOut.writeInt(revision);
    }

    @Override
    protected void readData(PacketInputStream dIn) throws IOException {
	baseX = dIn.readInt();
	baseY = dIn.readInt();
	baseZ = dIn.readInt();
	revision = dIn.readInt();
    }
    @Override
    public int getID() {
	return 5;
    }
}
