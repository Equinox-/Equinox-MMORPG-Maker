package com.pi.launcher;

import java.applet.Applet;

public class ClientLoader {
    private final static String className = "com.pi.client.clientviewer.ClientApplet";

    public static Applet loadClient() {
	ClientClassLoader cLoader = new ClientClassLoader();
	try {
	    return (Applet) cLoader.loadClass(className).newInstance();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }
}
