package com.pi.common.database.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

public class DatabaseIO {
    public static void write(OutputStream out, PacketObject obj)
	    throws IOException {
	PacketOutputStream pO = new PacketOutputStream(out);
	obj.writeData(pO);
	pO.close();
    }

    public static void write(File f, PacketObject obj) throws IOException {
	if (!f.exists())
	    f.createNewFile();
	write(new FileOutputStream(f), obj);
    }

    public static PacketObject read(InputStream in,
	    Class<? extends PacketObject> clazz) throws IOException {
	try {
	    PacketInputStream pIn = new PacketInputStream(in);
	    PacketObject obj = clazz.newInstance();
	    obj.readData(pIn);
	    pIn.close();
	    return obj;
	} catch (Exception e) {
	    throw new IOException("The database object is corrupted!");
	}
    }

    public static PacketObject read(File f,
	    Class<? extends PacketObject> clazz) throws IOException {
	return read(new FileInputStream(f), clazz);
    }
}
