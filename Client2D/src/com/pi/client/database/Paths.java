package com.pi.client.database;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import com.pi.common.database.SectorLocation;

/**
 * A class defining the locations of everything the client saves to the disk.
 * 
 * @author Westin
 * 
 */
public final class Paths {
	/**
	 * Accepted image extensions.
	 */
	public static final String[] IMAGE_FILES = { "gif", "jpg",
			"jpeg", "png" };

	/**
	 * The folder that the game cache is stored in.
	 */
	public static final String GAME_FOLDER_NAME =
			".equinox_mmorpg";

	/**
	 * An enum defining representing the possible operating systems.
	 * 
	 * @author Westin
	 * 
	 */
	public enum OperatingSystem {
		/**
		 * Represents the Mac operating system.
		 */
		MAC,
		/**
		 * Represents the Windows operating system.
		 */
		WINDOWS,
		/**
		 * Represents the Linux operating system.
		 */
		LINUX,
		/**
		 * Represents an unknown operating system.
		 */
		UNKNOWN;
		/**
		 * Looks up an operating system by name.
		 * 
		 * @param name the name to look up
		 * @return the operating system, or {@link OperatingSystem#UNKNOWN} if
		 *         not found.
		 */
		public static OperatingSystem lookup(final String name) {
			for (OperatingSystem sys : values()) {
				if (name.toUpperCase().contains(sys.name())) {
					return sys;
				}
			}
			return OperatingSystem.UNKNOWN;
		}
	}

	/**
	 * Represents the current operating system.
	 */
	public static final OperatingSystem CURRENT_OS =
			OperatingSystem
					.lookup(System.getProperty("os.name"));

	/**
	 * Get's the user's home directory on a *nix operating system.
	 * 
	 * @return the user's home directory.
	 */
	private static String getUnixHome() {
		final String home = System.getProperty("user.home");
		if (home == null) {
			return "~";
		} else {
			return home;
		}
	}

	/**
	 * Get's the local sector directory for caching the sectors from the server.
	 * 
	 * @return the sector directory
	 */
	public static File getSectorDirectory() {
		File f = new File(getHomeDirectory(), "world");
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Get's the local directory for caching the libraries.
	 * 
	 * @return the bin directory
	 */
	public static File getBinDirectory() {
		File f = new File(getHomeDirectory(), "bin");
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Get's the directory the OpenGL natives are stored in.
	 * 
	 * @return the natives directory
	 */
	public static File getNativesDirectory() {
		File f = new File(getBinDirectory(), "natives");
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the file for a sector at the given x and z on the given plane.
	 * 
	 * @param x the sector x location
	 * @param plane the plane
	 * @param z the sector z location
	 * @return the file path
	 */
	public static File getSectorFile(final int x,
			final int plane, final int z) {
		return new File(getSectorDirectory(), x + "-" + plane
				+ "-" + z + ".sector");
	}

	/**
	 * Gets the file for a sector at the given sector location.
	 * 
	 * @param l the sector location
	 * @return the file path
	 * @see Paths#getSectorFile(int, int, int)
	 */
	public static File getSectorFile(final SectorLocation l) {
		return getSectorFile(l.getSectorX(), l.getPlane(),
				l.getSectorZ());
	}

	/**
	 * Gets the home directory for the current operating system.
	 * <p>
	 * This is the User/Documents directory on Windows, and the ~/ directory on
	 * Mac and Linux.
	 * 
	 * @return the file path
	 */
	public static File getHomeDirectory() {
		File f;
		if (CURRENT_OS == OperatingSystem.WINDOWS) {
			f =
					new File(FileSystemView.getFileSystemView()
							.getDefaultDirectory(),
							GAME_FOLDER_NAME);
		} else {
			f = new File(getUnixHome(), GAME_FOLDER_NAME);
		}
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the path to a graphics file with the given id.
	 * 
	 * @param id the graphics id
	 * @return the file path
	 */
	public static File getGraphicsFile(final int id) {
		File gDir = getGraphicsDirectory();
		for (String ext : IMAGE_FILES) {
			File f = new File(gDir, id + "." + ext);
			if (f.exists()) {
				return f;
			}
		}
		return null;
	}

	/**
	 * Gets the path to the graphics directory.
	 * 
	 * @return the folder path
	 */
	public static File getGraphicsDirectory() {
		File f = new File(getHomeDirectory(), "graphics");
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the path to the log directory.
	 * 
	 * @return the folder path
	 */
	public static File getLogDirectory() {
		File f = new File(getHomeDirectory(), "log");
		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	/**
	 * Gets the log file.
	 * 
	 * @return the file path
	 */
	public static File getLogFile() {
		return new File(getLogDirectory(), "log");
	}

	/**
	 * Overridden constructor so the class can't be initiated.
	 */
	private Paths() {
	}
}
