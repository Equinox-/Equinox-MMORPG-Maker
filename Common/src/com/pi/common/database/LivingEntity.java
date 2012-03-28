package com.pi.common.database;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public abstract class LivingEntity extends Entity {
    private int health;

    public int getHealth() {
	return health;
    }

    @Override
    public void readData(PacketInputStream pIn) throws IOException {
	super.readData(pIn);
	health = pIn.readInt();
    }

    @Override
    public void writeData(PacketOutputStream pOut) throws IOException {
	super.writeData(pOut);
	pOut.writeInt(health);
    }

    @Override
    public int getLength() {
	return 4 + super.getLength();
    }
}
