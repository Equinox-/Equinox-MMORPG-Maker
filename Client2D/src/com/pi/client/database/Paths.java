package com.pi.client.database;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

public class Paths {
    public enum OperatingSystem {
	MAC, WINDOWS, LINUX, UNKNOWN;
	public static OperatingSystem lookup(String name) {
	    for (OperatingSystem sys : values()) {
		if (name.toLowerCase().contains(sys.name().toLowerCase()))
		    return sys;
	    }
	    return OperatingSystem.UNKNOWN;
	}
    }

    private static String[] imageFiles = { "gif", "jpg", "jpeg", "png" };
    private static OperatingSystem CURRENT_OS = OperatingSystem.lookup(System
	    .getProperty("os.name"));

    public static File getHomeDirectory() {
	File f = new File((CURRENT_OS == OperatingSystem.WINDOWS ? FileSystemView
		.getFileSystemView().getDefaultDirectory().getAbsolutePath()
		: getUnixHome())
		+ File.separator + ".equinox_mmorpg");
	if (!f.exists())
	    f.mkdir();
	return f;
    }

    private static String getUnixHome() {
	final String home = System.getProperty("user.home");
	return home == null ? "~" : home;
    }

    public static File getGraphicsDirectory() {
	File f = new File(getHomeDirectory(),"graphics");
	if (!f.exists())
	    f.mkdir();
	return f;
    }

    public static File getSectorDirectory() {
	File f = new File(getHomeDirectory(),"world");
	if (!f.exists())
	    f.mkdir();
	return f;
    }

    public static File getGraphicsFile(final String id) {
	File gDir = getGraphicsDirectory();
	for (String ext : imageFiles) {
	    File f = new File(gDir, id + "." + ext);
	    if (f.exists())
		return f;
	}
	return null;
    }

    public static File getSectorFile(int x, int y) {
	return new File(getSectorDirectory(), x + "-" + y + ".sector");
    }
}
