package com.pi.server.database.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.pi.common.database.def.EntityDef;

public class EntityDefIO {
    public static void write(OutputStream out, EntityDef def)
	    throws IOException {
	ObjectOutputStream objOut = new ObjectOutputStream(out);
	objOut.writeObject(def);
	objOut.close();
    }

    public static void write(File f, EntityDef def) throws IOException {
	if (!f.exists())
	    f.createNewFile();
	write(new FileOutputStream(f), def);
    }

    public static EntityDef read(InputStream in) throws IOException {
	try {
	    ObjectInputStream objIn = new ObjectInputStream(in);
	    EntityDef def = (EntityDef) objIn.readObject();
	    objIn.close();
	    return def;
	} catch (ClassNotFoundException e) {
	    throw new IOException("Unable to load entity def file!");
	} catch (ClassCastException e) {
	    throw new IOException("The entity def is corrupted!");
	}
    }

    public static EntityDef read(File f) throws IOException {
	return read(new FileInputStream(f));
    }
}
