package com.pi.launcher;

import java.io.IOException;

import javax.swing.JFrame;

import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;

/**
 * Class to display the client launcher in a frame.
 * 
 * @author Westin
 * 
 */
public final class LauncherViewerFrame {
	/**
	 * Create and display the client launcher in a frame.
	 * 
	 * @param args unused arguments
	 */
	public static void main(final String[] args) {
		JFrame plv = new JFrame("Launcher");
		plv.setSize(ServerConfiguration.DEFAULT_WIDTH,
				ServerConfiguration.DEFAULT_HEIGHT);
		plv.setLocation(0, 0);
		plv.setVisible(true);
		plv.setLayout(null);
		PILoggerPane plp = new PILoggerPane();
		plv.add(plp);
		plp.setSize(ServerConfiguration.DEFAULT_WIDTH,
				ServerConfiguration.DEFAULT_HEIGHT);
		plp.setLocation(0, 0);
		PILogger log = new PILogger(plp.getLogOutput());
		try {
			Updater.update(log);
		} catch (IOException e) {
			System.out.println("OK");
			e.printStackTrace();
		}
		if (ClientLoader.canRun()) {
			plv.setVisible(false);
			plv.dispose();
			ClientLoader.runClientFrame();
		}
	}

	/**
	 * Overridden constructor to allow instances to not be created.
	 */
	private LauncherViewerFrame() {
	}
}
