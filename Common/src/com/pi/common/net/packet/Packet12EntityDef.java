package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.database.def.EntityDef;
import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet12EntityDef extends Packet {
    public int entityID;
    public EntityDef def;

    @Override
    protected void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entityID);
	if (def != null) {
	    pOut.writeObject(def);
	} else {
	    pOut.writeObject(new Boolean(false));
	}
    }

    @Override
    protected void readData(PacketInputStream pIn) throws IOException {
	entityID = pIn.readInt();
	try {
	    Object defO = pIn.readObject();
	    if (defO instanceof EntityDef) {
		def = (EntityDef) defO;
	    } else {
		def = null;
	    }
	} catch (ClassNotFoundException e) {
	    throw new IOException("Bad Class: " + e.toString());
	}
    }

}
