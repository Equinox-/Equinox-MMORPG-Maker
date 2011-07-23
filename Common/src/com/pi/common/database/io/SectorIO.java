package com.pi.common.database.io;

import java.io.*;

import com.pi.common.database.Sector;

public class SectorIO {
    public static void write(OutputStream out, Sector sec) throws IOException {
	ObjectOutputStream objOut = new ObjectOutputStream(out);
	objOut.writeObject(sec);
	objOut.close();
    }

    public static void write(File f, Sector sec) throws IOException {
	if (!f.exists())
	    f.createNewFile();
	write(new FileOutputStream(f), sec);
    }
    
    public static Sector read(InputStream in) throws IOException {
	try {
	    ObjectInputStream objIn = new ObjectInputStream(in);
	    Sector sec = (Sector) objIn.readObject();
	    objIn.close();
	    return sec;
	} catch (ClassNotFoundException e) {
	    throw new IOException("Unable to load sector file!");
	} catch (ClassCastException e) {
	    throw new IOException("The sector is corrupted!");
	}
    }

    public static Sector read(File f) throws IOException {
	return read(new FileInputStream(f));
    }
}
