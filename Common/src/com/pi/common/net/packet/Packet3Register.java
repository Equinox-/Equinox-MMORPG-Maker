package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet3Register extends Packet {
    public String username;
    public String password;

    @Override
    protected void writeData(PacketOutputStream dOut) throws IOException {
	dOut.writeString(username);
	dOut.writeString(password);
    }

    @Override
    protected void readData(PacketInputStream dIn) throws IOException {
	username = dIn.readString();
	password = dIn.readString();
    }
    @Override
    public int getID() {
	return 3;
    }
}
