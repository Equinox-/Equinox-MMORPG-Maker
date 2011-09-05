package com.pi.common.net.packet;

import java.io.*;

import com.pi.common.database.Sector;
import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet4Sector extends Packet {
    public Sector sector;

    @Override
    protected void writeData(PacketOutputStream dOut) throws IOException {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	ObjectOutputStream objOut = new ObjectOutputStream(out);
	objOut.writeObject(sector);
	objOut.close();
	out.flush();
	dOut.writeByteArray(out.toByteArray());
	out.close();
    }

    @Override
    protected void readData(PacketInputStream dIn) throws IOException {
	byte[] byts = dIn.readByteArray();
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
