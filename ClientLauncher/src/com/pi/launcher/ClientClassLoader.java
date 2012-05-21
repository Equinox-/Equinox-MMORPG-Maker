package com.pi.launcher;

import java.io.File;
import java.net.URLClassLoader;

public class ClientClassLoader extends URLClassLoader {
	private static ClientClassLoader ldr;

	private ClientClassLoader() {
		super(Binaries.getJarURLs(), ClassLoader.getSystemClassLoader());
	}

	public static ClientClassLoader getClientClassLoader() {
		if (ldr == null) {
			ldr = new ClientClassLoader();
		}
		return ldr;
	}

	@Override
	public String findLibrary(String name) {
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
		}
		if (ext != null) {
			File f = new File(Paths.getNativesDirectory(), ext);
			if (f.exists())
				return f.getAbsolutePath();
		}
		File f = new File(Paths.getNativesDirectory(), name);
		if (f.exists())
			return f.getAbsolutePath();
		return null;
	}
}
