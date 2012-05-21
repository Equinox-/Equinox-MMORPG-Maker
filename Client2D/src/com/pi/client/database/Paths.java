package com.pi.client.database;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import com.pi.common.database.SectorLocation;

public class Paths {
	public final static String[] imageFiles = { "gif", "jpg", "jpeg", "png" };

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

	public static File getSectorDirectory() {
		File f = new File(getHomeDirectory(), "world");
		if (!f.exists())
			f.mkdir();
		return f;
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

	public static File getSectorFile(int x, int y, int z) {
		return new File(getSectorDirectory(), x + "-" + y + "-" + z + ".sector");
	}

	public static File getSectorFile(SectorLocation l) {
		return getSectorFile(l.x, l.plane, l.z);
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

	public static File getGraphicsFile(final int id) {
		File gDir = getGraphicsDirectory();
		for (String ext : imageFiles) {
			File f = new File(gDir, id + "." + ext);
			if (f.exists())
				return f;
			/*
			 * if (oldestID.endsWith(ext)) return new File(gDir, oldestID);
			 */
		}
		return null;
	}

	public static File getGraphicsDirectory() {
		File f = new File(getHomeDirectory(), "graphics");
		if (!f.exists())
			f.mkdir();
		return f;
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
