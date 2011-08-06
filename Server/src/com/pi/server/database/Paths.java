package com.pi.server.database;

import java.io.File;

import com.pi.common.database.SectorLocation;

public class Paths {
    public static File getHomeDirectory() {
	return new File("");
    }

    public static File getSectorDirectory() {
	File f = new File(getHomeDirectory(), "world");
	if (!f.exists())
	    f.mkdir();
	return f;
    }

    public static File getDatabaseFolder() {
	File f = new File(getHomeDirectory(), "database");
	if (!f.exists())
	    f.mkdir();
	return f;
    }

    public static File getAccountsDatabase() {
	File f = new File(getDatabaseFolder(), "accounts.db");
	if (!f.exists())
	    try {
		f.createNewFile();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	return f;
    }

    public static File getSectorFile(int x, int y, int z) {
	return new File(getSectorDirectory(), x + "-" + y + "-" + z + ".sector");
    }

    public static File getSectorFile(SectorLocation l) {
	return getSectorFile(l.x, l.y, l.z);
    }
}
