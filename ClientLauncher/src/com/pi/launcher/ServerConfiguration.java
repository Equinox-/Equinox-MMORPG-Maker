package com.pi.launcher;

/**
 * The client launcher configuration.
 * 
 * @author Westin
 * 
 */
public final class ServerConfiguration {
	/**
	 * The URL to the library folder.
	 */
	public static final String LIB_FOLDER =
			"https://raw.github.com/Equinox-/Equinox-MMORPG-Maker/master/ClientLauncher/lib/";

	/**
	 * Binary filenames to download, but not unzip.
	 * 
	 * @see #BINARY_KEYS
	 */
	public static final String[] BINARY_FILES = {
			"jogl.all.jar", "gluegen-rt.jar",
			"nativewindow.all.jar", "EquinoxClient.jar" };
	/**
	 * Binary keys to download, but not unzip.
	 * 
	 * @see #BINARY_FILES
	 */
	public static final String[] BINARY_KEYS = { "jogl",
			"gluegen", "nativewindow", "client" };

	/**
	 * A one kilobyte download cache.
	 */
	public static final int DOWNLOAD_CACHE = 1024;
	/**
	 * A two kilobyte unzip cache.
	 */
	public static final int UNZIP_CACHE = 1024;

	/**
	 * The default width of the client launcher.
	 */
	public static final int DEFAULT_WIDTH = 500;
	/**
	 * The default height of the client launcher.
	 */
	public static final int DEFAULT_HEIGHT = 500;

	/**
	 * Overridden constructor to prevent instances of this class.
	 */
	private ServerConfiguration() {
	}
}
