package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.Direction;
import com.pi.common.game.Entity;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet16EntityMove extends Packet {
    public int entity;
    public Direction direction;

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(entity);
	pOut.writeByte(direction.ordinal());
    }

    @Override
    public int getLength() {
	return 5;
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	entity = pIn.readInt();
	direction = Direction.values()[pIn.readByte()];
    }

    @Override
    public int getID() {
	return 16;
    }

    public static Packet create(Entity e, Direction dir) {
	Packet16EntityMove p = new Packet16EntityMove();
	p.entity = e.getEntityID();
	p.direction = dir;
	return p;
    }
}
