package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.game.Entity;
import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet9EntityData extends Packet {
    public Entity entity;

    @Override
    protected void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeObject(entity);
    }

    @Override
    protected void readData(PacketInputStream pIn) throws IOException {
	try {
	    entity = (Entity) pIn.readObject();
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	} catch (ClassCastException e) {
	    e.printStackTrace();
	}
    }

}
