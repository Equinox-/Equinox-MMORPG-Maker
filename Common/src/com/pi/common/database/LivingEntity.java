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
    public void read(PacketInputStream pIn) throws IOException {
	super.read(pIn);
	health = pIn.readInt();
    }

    @Override
    public void write(PacketOutputStream pOut) throws IOException {
	super.write(pOut);
	pOut.writeInt(health);
    }
}
