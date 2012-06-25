package com.pi.launcher;

import java.io.File;
import java.net.URLClassLoader;

/**
 * A class loader that uses the jar files loaded by the Updater class.
 * 
 * @author Westin
 * 
 */
public final class ClientClassLoader extends URLClassLoader {
	/**
	 * The cached client class loader.
	 */
	private static ClientClassLoader ldr;

	/**
	 * Creates the class loader instance for this updater, with a fallback of
	 * the system class loader.
	 * 
	 * @see Updater#getJarURLs()
	 */
	private ClientClassLoader() {
		super(Updater.getJarURLs(), ClassLoader
				.getSystemClassLoader());
	}

	/**
	 * Gets the class loader instance, or creates one if one doesn't exist.
	 * 
	 * @return the class loader instance
	 */
	public static ClientClassLoader getClientClassLoader() {
		if (ldr == null) {
			ldr = new ClientClassLoader();
		}
		return ldr;
	}

	@Override
	public String findLibrary(final String name) {
		String ext = null;
		switch (Paths.CURRENT_OS) {
		case LINUX:
			ext = "lib" + name + ".so";
			break;
		case MAC:
			ext = "lib" + name + ".jnilib";
			break;
		case WINDOWS:
			ext = name + ".dll";
			break;
		default:
			break;
		}
		if (ext != null) {
			File f = new File(Paths.getNativesDirectory(), ext);
			if (f.exists()) {
				return f.getAbsolutePath();
			}
		}
		File f = new File(Paths.getNativesDirectory(), name);
		if (f.exists()) {
			return f.getAbsolutePath();
		}
		return null;
	}
}
