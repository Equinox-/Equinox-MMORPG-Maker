package com.pi.server;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.BindException;

import javax.swing.JFrame;

import com.pi.common.database.Location;
import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;
import com.pi.common.debug.PIResourceViewer;
import com.pi.common.debug.SectorMonitorPanel;
import com.pi.common.debug.ThreadMonitorPanel;
import com.pi.server.client.ClientManager;
import com.pi.server.constants.ServerConstants;
import com.pi.server.database.Paths;
import com.pi.server.database.ServerDatabase;
import com.pi.server.debug.ClientMonitorPanel;
import com.pi.server.debug.EntityMonitorPanel;
import com.pi.server.def.Definitions;
import com.pi.server.entity.ServerEntityManager;
import com.pi.server.logic.ServerLogic;
import com.pi.server.net.NetServer;
import com.pi.server.world.SectorManager;

/**
 * The class managing all the server subsystems.
 * 
 * @see com.pi.server.net.NetServer
 * @see com.pi.server.world.SectorManager
 * @see com.pi.server.database.ServerDatabase
 * @see com.pi.server.entity.ServerEntityManager
 * @see com.pi.server.client.ClientManager
 * @see com.pi.server.def.Definitions
 * @author Westin
 * 
 */
public class Server {
	/**
	 * The thread group for monitoring all server threads.
	 */
	private ThreadGroup serverThreads;
	/**
	 * The server's network model.
	 */
	private NetServer network;
	/**
	 * The sector manager instance.
	 */
	private SectorManager world;
	/**
	 * The message logger.
	 */
	private PILogger log;
	/**
	 * The server's database model.
	 */
	private ServerDatabase database;
	/**
	 * The server's entity management system.
	 */
	private ServerEntityManager entityManager;
	/**
	 * The server's client management system.
	 */
	private ClientManager clientManager;
	/**
	 * The server's definitions loader.
	 */
	private Definitions defs;
	/**
	 * The resource viewer instance for this server.
	 */
	private PIResourceViewer rcView;
	/**
	 * The server logic that runs all automated tasks.
	 */
	private ServerLogic sLogic;
	/**
	 * If this server instance is being disposed. This flag prevents the sub
	 * systems from being disposed twice.
	 */
	private boolean disposing = false;

	/**
	 * Gets the server's network model instance.
	 * 
	 * @return the network model
	 */
	public final NetServer getNetwork() {
		return network;
	}

	/**
	 * Gets the server's database model instance.
	 * 
	 * @return the database model
	 */
	public final ServerDatabase getDatabase() {
		return database;
	}

	/**
	 * Gets the client management systems,.
	 * 
	 * @return the client manager
	 */
	public final ClientManager getClientManager() {
		return clientManager;
	}

	/**
	 * The message logger.
	 * 
	 * @return the logger
	 */
	public final PILogger getLog() {
		return log;
	}

	/**
	 * Creates a server and all of it's subsystems.
	 */
	public Server() {
		serverThreads = new ThreadGroup("Server");
		rcView = new PIResourceViewer("Server");
		PILoggerPane pn = new PILoggerPane();
		rcView.addTab("Logger", pn);
		rcView.addTab("Threads", new ThreadMonitorPanel(serverThreads));
		rcView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		rcView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				dispose();
			}
		});
		log = new PILogger(pn.getLogOutput(), Paths.getLogFile());

		entityManager = new ServerEntityManager(this);
		rcView.addTab("Entities", new EntityMonitorPanel(this));
		clientManager = new ClientManager();
		database = new ServerDatabase(this);
		try {
			network = new NetServer(this, ServerConstants.NETWORK_PORT);
			rcView.addTab("Network Clients", new ClientMonitorPanel(
					clientManager));
			world = new SectorManager(this);
			rcView.addTab("Sectors", new SectorMonitorPanel(world));
			defs = new Definitions(this);

			sLogic = new ServerLogic(this);
			sLogic.start();

			entityManager.spawnEntity(defs.getEntityLoader().getDef(1),
					new Location());

			// entityManager.spawnItemEntity(0, new Location(5, 0,
			// 5));

		} catch (BindException e1) {
			dispose();
		}
	}

	/**
	 * Disposes all the subsystems of this server.
	 */
	@SuppressWarnings("deprecation")
	public final void dispose() {
		if (!disposing) {
			disposing = true;
			if (sLogic != null) {
				sLogic.dispose();
			}
			if (network != null) {
				network.dispose();
			}
			if (world != null) {
				world.dispose();
			}
			if (database != null) {
				database.save();
			}
		} else {
			if (rcView != null) {
				rcView.dispose();
			}

			serverThreads.stop();
			log.close();
		}
	}

	/**
	 * Gets the entity manager bound to this server.
	 * 
	 * @return the entity manager
	 */
	public final ServerEntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Launches the default server instance.
	 * 
	 * @param args
	 *            unused
	 */
	public static void main(final String[] args) {
		new Server();
	}

	/**
	 * Gets the server's world model.
	 * 
	 * @return the sector manager
	 */
	public final SectorManager getWorld() {
		return world;
	}

	/**
	 * Gets the server's definitions model.
	 * 
	 * @return the definitions loader
	 */
	public final Definitions getDefs() {
		return defs;
	}

	/**
	 * Gets this server's thread registration group.
	 * 
	 * @return the thread group
	 */
	public final ThreadGroup getThreadGroup() {
		return serverThreads;
	}

	/**
	 * Is the server's network model initialized and running.
	 * 
	 * @return if the network is connected
	 */
	public final boolean isNetworkConnected() {
		return network.isConnected();
	}

	public final ServerLogic getLogic() {
		return sLogic;
	}
}
