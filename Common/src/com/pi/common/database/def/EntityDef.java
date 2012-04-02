package com.pi.common.database.def;

import java.io.IOException;

import com.pi.common.database.GraphicsObject;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class EntityDef extends GraphicsObject {
    private int defID;
    private int horizFrames = 4;

    public int getHorizontalFrames() {
	return horizFrames;
    }

    public void setHorizontalFrames(int f) {
	this.horizFrames = f;
    }

    public int getDefID() {
	return defID;
    }

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	super.writeData(pOut);
	pOut.writeInt(horizFrames);
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	super.readData(pIn);
	horizFrames = pIn.readInt();
    }

    @Override
    public int getLength() {
	return super.getLength() + 4;
    }
}
