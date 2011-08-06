package com.pi.common.net.packet;

import java.io.*;

public class Packet6BlankSector extends Packet {
    public int baseX;
    public int baseY;
    public int baseZ;

    @Override
    protected void writeData(DataOutputStream dOut) throws IOException {
	dOut.writeInt(baseX);
	dOut.writeInt(baseY);
	dOut.writeInt(baseZ);
    }

    @Override
    protected void readData(DataInputStream dIn) throws IOException {
	baseX = dIn.readInt();
	baseY = dIn.readInt();
	baseZ = dIn.readInt();
    }
}
