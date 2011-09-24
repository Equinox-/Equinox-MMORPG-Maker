package com.pi.client;

import java.applet.Applet;

import javax.swing.JOptionPane;

import com.pi.client.database.Paths;
import com.pi.client.database.webfiles.GraphicsLoader;
import com.pi.client.debug.GraphicsMonitorPanel;
import com.pi.client.debug.SectorMonitorPanel;
import com.pi.client.graphics.device.DisplayManager;
import com.pi.client.gui.GUIKit;
import com.pi.client.net.NetClientClient;
import com.pi.client.world.World;
import com.pi.common.Disposable;
import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;
import com.pi.common.debug.PIResourceViewer;
import com.pi.common.debug.ThreadMonitorPanel;
import com.pi.common.game.Entity;

public class Client implements Disposable {
	static {
		GUIKit.init();
	}
	private ThreadGroup clientThreads;
	private String ip;
	private int port;
	private boolean inGame = false;
	private Applet cApplet;
	private DisplayManager displayManager;
	private World world;
	public Entity player;
	private NetClientClient network;
	private final PILogger logger;
	PIResourceViewer reView;

	public Client(Applet applet) {
		clientThreads = new ThreadGroup("ClientThreads");
		reView = new PIResourceViewer("Client");
		PILoggerPane plp = new PILoggerPane();
		logger = new PILogger(Paths.getLogFile(), plp.logOut);
		reView.addTab("Logger", plp);
		reView.addTab("Threads", new ThreadMonitorPanel(clientThreads));
		ip = "127.0.0.1"/*JOptionPane.showInputDialog("IP?")*/;
		try {
			port = 9999/*Integer.valueOf(JOptionPane.showInputDialog("Port?"))*/;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Bad port number");
			System.exit(0);
		}
		this.cApplet = applet;
		this.player = new Entity();
		this.player.x = 0;
		this.player.z = 0;
		this.player.plane = 0;
		this.displayManager = new DisplayManager(this);
		reView.addTab("Graphics",
				new GraphicsMonitorPanel(this.displayManager));
		GraphicsLoader.load(this);
		this.world = new World(this);
		reView.addTab("Sectors",
				new SectorMonitorPanel(this.world.getSectorManager()));
		network = new NetClientClient(this, ip, port);
		this.displayManager.postInititation();
	}

	public NetClientClient getNetwork() {
		return network;
	}

	public Applet getApplet() {
		return this.cApplet;
	}

	public DisplayManager getDisplayManager() {
		return displayManager;
	}

	public World getWorld() {
		return world;
	}

	public PILogger getLog() {
		return logger;
	}

	@Override
	public void dispose() {
		logger.close();
		if (reView != null) {
			reView.dispose();
		}
		displayManager.dispose();
		world.dispose();
		if (network != null)
			network.dispose();
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean val) {
		this.inGame = val;
	}

	public boolean isNetworkConnected() {
		return getNetwork() != null && getNetwork().getSocket() != null
				&& getNetwork().getSocket().isConnected()
				&& !getNetwork().getSocket().isClosed();
	}

	public String getNetworkIP() {
		return ip;
	}

	public int getNetworkPort() {
		return port;
	}

	public ThreadGroup getThreadGroup() {
		return clientThreads;
	}
}
