package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.Direction;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet14ClientMove extends Packet {
    public Direction direction;

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	pOut.writeByte(direction.ordinal());
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	direction = Direction.values()[pIn.readByte()];
    }

    public static Packet14ClientMove create(Direction dir) {
	Packet14ClientMove pack = new Packet14ClientMove();
	pack.direction = dir;
	return pack;
    }

    @Override
    public int getID() {
	return 14;
    }

    @Override
    public int getLength() {
	return 1;
    }
}
