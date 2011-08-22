package com.pi.launcher;

import java.io.IOException;

import com.pi.common.PILogViewer;
import com.pi.common.PILogger;

public class LauncherViewerFrame {
    public static void main(String[] args) {
	PILogViewer plv = new PILogViewer("Launcher");
	PILogger log = new PILogger(plv.pane.logOut);
	try {
	    Updater.update(log);
	    plv.setVisible(false);
	    plv.dispose();
	    ClientLoader.runClientFrame();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
