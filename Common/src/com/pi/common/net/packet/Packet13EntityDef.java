package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.def.EntityDef;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet13EntityDef extends Packet {
    public int entityID;
    public EntityDef def;

    @Override
    protected void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entityID);
	def.write(pOut);
    }

    @Override
    protected void readData(PacketInputStream pIn) throws IOException {
	entityID = pIn.readInt();
	if (def == null)
	    def = new EntityDef();
	def.read(pIn);
    }

    @Override
    public int getID() {
	return 13;
    }
}
