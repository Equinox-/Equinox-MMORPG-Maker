package com.pi.server.database;

import java.io.File;

import com.pi.common.database.SectorLocation;

public class Paths {
    public static File getHomeDirectory() {
	File f = new File("equinox-server");
	if (!f.isDirectory())
	    f.mkdir();
	return f;
    }

    public static File getSectorDirectory() {
	File f = new File(getHomeDirectory(), "world");
	if (!f.isDirectory())
	    f.mkdir();
	return f;
    }

    public static File getDefDirectory() {
	File f = new File(getHomeDirectory(), "def");
	if (!f.isDirectory())
	    f.mkdir();
	return f;
    }

    public static File getDatabaseFolder() {
	File f = new File(getHomeDirectory(), "database");
	if (!f.isDirectory())
	    f.mkdir();
	return f;
    }

    public static File getAccountsDatabase() {
	File f = new File(getDatabaseFolder(), "accounts.db");
	if (!f.exists()) {
	    System.out.println(f.getAbsolutePath());
	    try {
		f.createNewFile();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
	return f;
    }

    public static File getSectorFile(int x, int y, int z) {
	return new File(getSectorDirectory(), x + "-" + y + "-" + z + ".sector");
    }

    public static File getSectorFile(SectorLocation l) {
	return getSectorFile(l.x, l.plane, l.z);
    }

    public static File getEntityDefDirectory() {
	File f = new File(getDefDirectory(), "entity");
	if (!f.exists())
	    f.mkdir();
	return f;
    }

    public static File getEntityDef(int def) {
	return new File(getEntityDefDirectory(), def + ".def");
    }

    public static File getLogDirectory() {
	File f = new File(getHomeDirectory(), "log");
	if (!f.exists())
	    f.mkdir();
	return f;
    }

    public static File getLogFile() {
	return new File(getLogDirectory(), "log");
    }
}
