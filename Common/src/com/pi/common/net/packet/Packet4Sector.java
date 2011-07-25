package com.pi.common.net.packet;

import java.io.*;

import com.pi.common.database.Sector;

public class Packet4Sector extends Packet {
    public Sector sector;

    @Override
    protected void writeData(DataOutputStream dOut) throws IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	ObjectOutputStream objOut = new ObjectOutputStream(out);
	objOut.writeObject(sector);
	objOut.close();
	byte[] byts = out.toByteArray();
	dOut.writeInt(byts.length);
	dOut.write(byts);
	out.close();
    }

    @Override
    protected void readData(DataInputStream dIn) throws IOException {
	byte[] byts = new byte[dIn.readInt()];
	dIn.read(byts);
	ByteArrayInputStream in = new ByteArrayInputStream(byts);
	ObjectInputStream objIn = new ObjectInputStream(in);
	try {
	    sector = (Sector) objIn.readObject();
	} catch (ClassNotFoundException e) {
	    sector = null;
	}
	objIn.close();
	in.close();
    }

}
