package com.pi.common.net.packet;

import java.io.*;

public class Packet5SectorRequest extends Packet {
    public int baseX;
    public int baseY;
    public int baseZ;
    public int revision;

    @Override
    protected void writeData(DataOutputStream dOut) throws IOException {
	dOut.writeInt(baseX);
	dOut.writeInt(baseY);
	dOut.writeInt(baseZ);
	dOut.writeInt(revision);
    }

    @Override
    protected void readData(DataInputStream dIn) throws IOException {
	baseX = dIn.readInt();
	baseY = dIn.readInt();
	baseZ = dIn.readInt();
	revision = dIn.readInt();
    }
}
