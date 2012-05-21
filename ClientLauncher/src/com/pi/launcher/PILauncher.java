package com.pi.launcher;

import java.applet.Applet;
import java.io.IOException;

import com.pi.common.Disposable;
import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;

public class PILauncher extends Applet {
	private static final long serialVersionUID = 1L;
	PILogger log;
	PILoggerPane pane;
	Disposable bound;

	public PILauncher() {
		setSize(500, 500);
		setLayout(null);
		pane = new PILoggerPane();
		pane.setSize(500, 500);
		pane.setLocation(0, 0);
		add(pane);
		log = new PILogger(pane.logOut);
	}

	@Override
	public void start() {
		try {
			Updater.update(log);
			bound = ClientLoader.loadClientApplet(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		if (bound != null)
			bound.dispose();
		super.destroy();
	}
}
