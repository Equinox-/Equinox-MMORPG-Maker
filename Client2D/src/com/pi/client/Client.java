package com.pi.client;

import java.net.ConnectException;

import com.pi.client.clientviewer.ClientApplet;
import com.pi.client.database.webfiles.GraphicsLoader;
import com.pi.client.graphics.device.DisplayManager;
import com.pi.client.gui.GUIKit;
import com.pi.client.net.NetClientClient;
import com.pi.client.world.World;
import com.pi.common.PILogViewer;
import com.pi.common.PILogger;
import com.pi.common.database.Entity;

public class Client {
    static {
	GUIKit.init();
    }
    private boolean inGame = false;
    private ClientApplet cApplet;
    private DisplayManager displayManager;
    private World world;
    public Entity player;
    private NetClientClient network;
    private final PILogger logger;
    private final boolean logViewer = true;
    private final PILogViewer viewerFrame;

    public Client(ClientApplet applet) {
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
	try {
	    network = new NetClientClient(this, "127.0.0.1", 9999);
	} catch (ConnectException e) {
	    if (network != null)
		network.dispose();
	    network = null;
	}
	this.displayManager.postInititation();
    }

    public NetClientClient getNetwork() {
	return network;
    }

    public ClientApplet getApplet() {
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
}
