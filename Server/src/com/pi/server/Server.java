package com.pi.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;
import com.pi.common.debug.PIResourceViewer;
import com.pi.common.debug.ThreadMonitorPanel;
import com.pi.server.client.ClientManager;
import com.pi.server.database.Paths;
import com.pi.server.database.ServerDatabase;
import com.pi.server.debug.ClientMonitorPanel;
import com.pi.server.debug.EntityMonitorPanel;
import com.pi.server.debug.SectorMonitorPanel;
import com.pi.server.def.Definitions;
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
    private Definitions defs;
    private PIResourceViewer rcView;
    private boolean disposing = false;

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
	rcView = new PIResourceViewer("Server");
	PILoggerPane pn = new PILoggerPane();
	rcView.addTab("Logger", pn);
	rcView.addTab("Threads", new ThreadMonitorPanel(serverThreads));
	rcView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	rcView.addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		if (!disposing)
		    dispose();
	    }
	});
	log = new PILogger(Paths.getLogFile(), pn.logOut);
	
	entityManager = new ServerEntityManager(this);
	rcView.addTab("Entities", new EntityMonitorPanel(entityManager));
	clientManager = new ClientManager();
	database = new ServerDatabase(this);
	network = new NetServer(this, 9999);
	rcView.addTab("Network Clients", new ClientMonitorPanel(clientManager));
	world = new World(this);
	rcView.addTab("Sectors",
		new SectorMonitorPanel(world.getSectorManager()));
	defs = new Definitions(this);
    }

    @SuppressWarnings("deprecation")
    public void dispose() {
	if (!disposing) {
	    disposing = true;
	    if (network != null)
		network.dispose();
	    if (database != null)
		database.save();
	    if (world != null)
		world.dispose();
	    if (defs != null)
		defs.dispose();
	    if (rcView != null)
		rcView.dispose();

	    serverThreads.stop();
	    log.close();
	}
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

    public Definitions getDefs() {
	return defs;
    }

    public ThreadGroup getThreadGroup() {
	return serverThreads;
    }

    public boolean isNetworkConnected() {
	return network.isConnected();
    }
}
