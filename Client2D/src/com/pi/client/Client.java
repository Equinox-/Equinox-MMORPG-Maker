package com.pi.client;

import java.applet.Applet;
import java.net.ConnectException;

import javax.swing.JOptionPane;

import com.pi.client.database.webfiles.GraphicsLoader;
import com.pi.client.graphics.device.DisplayManager;
import com.pi.client.gui.GUIKit;
import com.pi.client.net.NetClientClient;
import com.pi.client.world.World;
import com.pi.common.*;
import com.pi.common.database.Entity;

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
    private final boolean logViewer = true;
    private final PILogViewer viewerFrame;

    public Client(Applet applet) {
	clientThreads = new ThreadGroup("ClientThreads");
	ip = JOptionPane.showInputDialog("IP?");
	try {
	    port = Integer.valueOf(JOptionPane.showInputDialog("Port?"));
	} catch (Exception e) {
	    JOptionPane.showMessageDialog(null, "Bad port number");
	    System.exit(0);
	}
	if (logViewer) {
	    viewerFrame = new PILogViewer("Client");
	    logger = new PILogger(viewerFrame.pane.logOut);
	} else {
	    viewerFrame = null;
	    logger = new PILogger();
	}
	this.cApplet = applet;
	this.player = new Entity();
	this.player.x = 0;
	this.player.z = 0;
	this.player.plane = 0;
	this.displayManager = new DisplayManager(this);
	GraphicsLoader.load(this);
	this.world = new World(this);
	network = new NetClientClient(this);
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
	if (viewerFrame != null) {
	    viewerFrame.setVisible(false);
	    viewerFrame.dispose();
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
