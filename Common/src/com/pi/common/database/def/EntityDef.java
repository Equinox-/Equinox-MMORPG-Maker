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

    public int getDefID() {
	return defID;
    }

    @Override
    public void write(PacketOutputStream pOut) throws IOException {
	super.write(pOut);
	pOut.writeInt(defID);
	pOut.writeInt(horizFrames);
    }

    @Override
    public void read(PacketInputStream pIn) throws IOException {
	super.read(pIn);
	defID = pIn.readInt();
	horizFrames = pIn.readInt();
    }
}
