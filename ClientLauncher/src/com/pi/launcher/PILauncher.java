package com.pi.launcher;

import java.applet.Applet;
import java.io.IOException;

import com.pi.common.PILogViewer;
import com.pi.common.PILogger;

public class PILauncher extends Applet{
    private static final long serialVersionUID = 1L;
    PILogger log;
    PILogViewer.LogPane pane;

    public PILauncher() {
	setSize(500, 500);
	pane = new PILogViewer.LogPane();
	pane.setSize(500,450);
	pane.setLocation(0, 50);
	add(pane);
	log = new PILogger(pane.logOut);
	try {
	    Updater.update(log);
	    Applet cApp = ClientLoader.loadClient();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
    public static void main(String[] args){
	PILogViewer plv = new PILogViewer("Launcher");
	PILogger log = new PILogger(plv.pane.logOut);
	try {
	    Updater.update(log);
	    Applet applet = ClientLoader.loadClient();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
