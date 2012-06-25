package com.pi.launcher;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

/**
 * A class defining the locations of everything the client launcher saves to the
 * disk.
 * 
 * @author Westin
 * 
 */
public final class Paths {
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
	 * Get's the user's home directory on a unix operating system.
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
	 * Overridden constructor so the class can't be initiated.
	 */
	private Paths() {
	}
}