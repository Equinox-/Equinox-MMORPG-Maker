package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet6BlankSector extends Packet {
    public int baseX;
    public int baseY;
    public int baseZ;

    @Override
    public void writeData(PacketOutputStream dOut) throws IOException {
	dOut.writeInt(baseX);
	dOut.writeInt(baseY);
	dOut.writeInt(baseZ);
    }

    @Override
    public void readData(PacketInputStream dIn) throws IOException {
	baseX = dIn.readInt();
	baseY = dIn.readInt();
	baseZ = dIn.readInt();
    }
    @Override
    public int getID() {
	return 6;
    }

    @Override
    public int getLength() {
	return 12;
    }
}
