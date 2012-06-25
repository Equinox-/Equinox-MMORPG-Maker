package com.pi.launcher;

import java.applet.Applet;

import com.pi.common.Disposable;

/**
 * This utility class loads and runs the client as a frame or an applet.
 * 
 * @author Westin
 * 
 */
public final class ClientLoader {
	/**
	 * The class to load and run for applet based viewing.
	 */
	private static final String APPLET_CLASS =
			"com.pi.client.Client";
	/**
	 * The class to load and run for frame based viewing.
	 */
	private static final String FRAME_CLASS =
			"com.pi.client.clientviewer.ClientViewerFrame";

	/**
	 * Loads the client as an applet, and returns the result.
	 * 
	 * @param bind the applet to bind the client to.
	 * @return the client instance.
	 */
	public static Disposable loadClientApplet(final Applet bind) {
		ClientClassLoader cLoader =
				ClientClassLoader.getClientClassLoader();
		try {
			bind.removeAll();
			return (Disposable) cLoader.loadClass(APPLET_CLASS)
					.getConstructor(Applet.class)
					.newInstance(bind);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Checks if the client is able to be launched.
	 * 
	 * @see Updater#hasBinaries()
	 * @see Updater#hasNatives()
	 * @return if the client is launchable
	 */
	public static boolean canRun() {
		return Updater.hasBinaries() && Updater.hasNatives();
	}

	/**
	 * Runs the client as a frame.
	 */
	public static void runClientFrame() {
		ClientClassLoader cLoader =
				ClientClassLoader.getClientClassLoader();
		Object[] args = new Object[] { new String[] {} };
		try {
			cLoader.loadClass(FRAME_CLASS)
					.getMethod("main", String[].class)
					.invoke(null, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Overridden constructor to prevent instances of this class being created.
	 */
	private ClientLoader() {
	}
}
