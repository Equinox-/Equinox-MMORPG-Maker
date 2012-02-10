package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class Packet1Login extends Packet {
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
	return 1;
    }

    @Override
    public int getLength() {
	return 8 + username.length() + password.length();
    }
}
