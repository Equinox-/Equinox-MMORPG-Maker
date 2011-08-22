package com.pi.launcher;

import java.applet.Applet;

public class ClientLoader {
    private final static String appletName = "com.pi.client.clientviewer.ClientApplet";
    private final static String frameName = "com.pi.client.clientviewer.ClientViewerFrame";

    public static Applet loadClientApplet() {
	ClientClassLoader cLoader = new ClientClassLoader();
	try {
	    return (Applet) cLoader.loadClass(appletName).newInstance();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static void runClientFrame() {
	ClientClassLoader cLoader = new ClientClassLoader();
	Object[] args = new Object[] { new String[] {} };
	try {
	    cLoader.loadClass(frameName).getMethod("main", String[].class)
		    .invoke(null, args);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
