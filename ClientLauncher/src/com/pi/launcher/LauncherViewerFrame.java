package com.pi.launcher;

import java.io.IOException;

import javax.swing.JFrame;

import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;

public class LauncherViewerFrame {
	public static void main(String[] args) {
		JFrame plv = new JFrame("Launcher");
		plv.setSize(500, 500);
		plv.setLocation(0, 0);
		plv.setVisible(true);
		plv.setLayout(null);
		PILoggerPane plp = new PILoggerPane();
		plv.add(plp);
		plp.setSize(500, 500);
		plp.setLocation(0, 0);
		PILogger log = new PILogger(plp.logOut);
		try {
			Updater.update(log);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (ClientLoader.canRun()) {
			plv.setVisible(false);
			plv.dispose();
			ClientLoader.runClientFrame();
		}
	}
}
