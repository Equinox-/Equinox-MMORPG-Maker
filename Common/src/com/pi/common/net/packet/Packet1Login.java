package com.pi.common.net.packet;

import java.io.*;

public class Packet1Login extends Packet {
    public String username;
    public String password;

    @Override
    protected void writeData(DataOutputStream dOut) throws IOException {
	super.writeString(dOut, username);
	super.writeString(dOut, password);
    }

    @Override
    protected void readData(DataInputStream dIn) throws IOException {
	username = super.readString(dIn);
	password = super.readString(dIn);
    }
}
