package com.pi.client.constants;

/**
 * All constants regarding client side operations.
 * 
 * @author Westin
 * 
 */
public abstract class Constants {
	/**
	 * The address to establish a network connection on.
	 */
	public static final String NETWORK_IP = "127.0.0.1";
	/**
	 * The port to establish a network connection on.
	 */
	public static final int NETWORK_PORT = 9999;

	/**
	 * The default width of the client viewing container.
	 */
	public static final int DEFAULT_CLIENT_WIDTH = 500;
	/**
	 * The default height of the client viewing container.
	 */
	public static final int DEFAULT_CLIENT_HEIGHT = 500;

	/**
	 * The base graphics URL to update graphics with.
	 */
	public static final String GRAPHICS_URL =
			"https://raw.github.com/Equinox-/Equinox-MMORPG-Maker/master/Client2D/graphics/";
	/**
	 * The graphics file list URL to update graphics with. If this is null, no
	 * updating will occur.
	 */
	public static final String GRAPHICS_FILELIST = GRAPHICS_URL
			+ "filelist";
}
