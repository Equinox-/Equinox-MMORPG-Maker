package com.pi.launcher;

import java.applet.Applet;
import java.io.IOException;

import com.pi.common.Disposable;
import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;

/**
 * A launcher class that uses an applet to display the client.
 * 
 * @author Westin
 * 
 */
public class PILauncher extends Applet {
	/**
	 * The logger that progress messages are printed into.
	 */
	private PILogger log;
	/**
	 * The logger pane that the logger outputs to.
	 */
	private PILoggerPane pane;
	/**
	 * The bound disposable client.
	 */
	private Disposable bound;

	/**
	 * Create a launcher applet.
	 */
	public PILauncher() {
		setSize(ServerConfiguration.DEFAULT_WIDTH,
				ServerConfiguration.DEFAULT_HEIGHT);
		setLayout(null);
		pane = new PILoggerPane();
		pane.setSize(ServerConfiguration.DEFAULT_WIDTH,
				ServerConfiguration.DEFAULT_HEIGHT);
		pane.setLocation(0, 0);
		add(pane);
		log = new PILogger(pane.getLogOutput());
	}

	@Override
	public final void start() {
		try {
			Updater.update(log);
			bound = ClientLoader.loadClientApplet(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public final void destroy() {
		if (bound != null) {
			bound.dispose();
		}
		super.destroy();
	}
}
