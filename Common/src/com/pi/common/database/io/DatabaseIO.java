package com.pi.common.database.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.pi.common.database.DatabaseObject;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class DatabaseIO {
    public static void write(OutputStream out, DatabaseObject obj)
	    throws IOException {
	PacketOutputStream pO = new PacketOutputStream(out);
	obj.write(pO);
	pO.close();
    }

    public static void write(File f, DatabaseObject obj) throws IOException {
	if (!f.exists())
	    f.createNewFile();
	write(new FileOutputStream(f), obj);
    }

    public static DatabaseObject read(InputStream in,
	    Class<? extends DatabaseObject> clazz) throws IOException {
	try {
	    PacketInputStream pIn = new PacketInputStream(in);
	    DatabaseObject obj = clazz.newInstance();
	    obj.read(pIn);
	    pIn.close();
	    return obj;
	} catch (Exception e) {
	    throw new IOException("The database object is corrupted!");
	}
    }

    public static DatabaseObject read(File f,
	    Class<? extends DatabaseObject> clazz) throws IOException {
	return read(new FileInputStream(f), clazz);
    }
}
