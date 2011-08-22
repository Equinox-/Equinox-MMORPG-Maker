package com.pi.launcher;


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

    public static OperatingSystem CURRENT_OS = OperatingSystem.lookup(System
	    .getProperty("os.name"));

    public static String getUnixHome() {
	final String home = System.getProperty("user.home");
	return home == null ? "~" : home;
    }
    public static File getBinDirectory() {
	File f = new File(getHomeDirectory(), "bin");
	if (!f.exists())
	    f.mkdir();
	return f;
    }
    public static File getNativesDirectory() {
	File f = new File(getBinDirectory(), "natives");
	if (!f.exists())
	    f.mkdir();
	return f;
    }

    public static File getHomeDirectory() {
	File f = new File(
		(CURRENT_OS == OperatingSystem.WINDOWS ? FileSystemView
			.getFileSystemView().getDefaultDirectory()
			.getAbsolutePath() : getUnixHome())
			+ File.separator + ".equinox_mmorpg");
	if (!f.exists())
	    f.mkdir();
	return f;
    }
}