package com.pi.client;

import java.net.ConnectException;

import com.pi.client.clientviewer.ClientApplet;
import com.pi.client.graphics.device.DisplayManager;
import com.pi.client.gui.GUIKit;
import com.pi.client.net.NetClientClient;
import com.pi.client.world.World;
import com.pi.common.PILogger;
import com.pi.common.database.Entity;
import com.pi.database.webfiles.GraphicsLoader;

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
    private final PILogger logger = new PILogger();

    public Client(ClientApplet applet) {
	this.cApplet = applet;
	this.player = new Entity();
	this.player.moveTo(0, 0);
	this.displayManager = new DisplayManager(this);
	GraphicsLoader.load(this);
	this.world = new World(this);
	try {
	    network = new NetClientClient(this, "127.0.0.1", 9999);
	} catch (ConnectException e) {
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

    public void destroy() {
	displayManager.dispose();
	world.dispose();
    }

    public boolean isInGame() {
	return inGame;
    }

    public boolean isNetworkConnected() {
	return getNetwork() != null && getNetwork().getSocket() != null
		&& getNetwork().getSocket().isConnected()
		&& !getNetwork().getSocket().isClosed();
    }
}
