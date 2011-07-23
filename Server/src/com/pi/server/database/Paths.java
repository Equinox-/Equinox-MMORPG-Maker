package com.pi.server.database;

import java.io.File;

public class Paths {
    public static File getSectorDirectory() {
	File f = new File("world");
	if (!f.exists())
	    f.mkdir();
	return f;
    }

    public static File getDatabaseFolder() {
	File f = new File("database");
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

    public static File getSectorFile(int x, int y) {
	return new File(getSectorDirectory(), x + "-" + y + ".sector");
    }
}
