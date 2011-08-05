package com.pi.common.net.packet;

import java.io.*;

public class Packet6BlankSector extends Packet {
    public int baseX;
    public int baseY;

    @Override
    protected void writeData(DataOutputStream dOut) throws IOException {
	dOut.writeInt(baseX);
	dOut.writeInt(baseY);
    }

    @Override
    protected void readData(DataInputStream dIn) throws IOException {
	baseX = dIn.readInt();
	baseY = dIn.readInt();
    }
}
