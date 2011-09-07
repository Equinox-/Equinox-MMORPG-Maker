package com.pi.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.pi.common.PILogViewer;
import com.pi.common.PILogger;
import com.pi.server.client.ClientManager;
import com.pi.server.database.Paths;
import com.pi.server.database.ServerDatabase;
import com.pi.server.entity.ServerEntityManager;
import com.pi.server.net.NetServer;
import com.pi.server.world.World;

public class Server {
    private ThreadGroup serverThreads;
    private NetServer network;
    private World world;
    private PILogger log;
    private ServerDatabase database;
    private ServerEntityManager entityManager;
    private ClientManager clientManager;

    public NetServer getNetwork() {
	return network;
    }

    public ServerDatabase getDatabase() {
	return database;
    }

    public ClientManager getClientManager() {
	return clientManager;
    }

    public PILogger getLog() {
	return log;
    }

    public Server() {
	serverThreads = new ThreadGroup("Server");
	try {
	    PILogViewer f = new PILogViewer("Server");
	    f.addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
		    dispose();
		}
	    });
	    log = new PILogger(Paths.getLogFile(), f.pane.logOut);
	    int port = Integer.valueOf(9999);
	    database = new ServerDatabase(this);
	    network = new NetServer(this, port, null);
	    world = new World(this);
	    entityManager = new ServerEntityManager(this);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void dispose() {
	log.close();
	if (network != null)
	    network.dispose();
	if (database != null)
	    database.save();
	if (world != null)
	    world.dispose();
    }

    public ServerEntityManager getServerEntityManager() {
	return entityManager;
    }

    public static void main(String[] args) {
	new Server();
    }

    public World getWorld() {
	return world;
    }

    public ThreadGroup getThreadGroup() {
	return serverThreads;
    }
}
