package com.pi.client;

import java.applet.Applet;
import java.awt.Container;
import java.io.File;

import com.pi.client.constants.Constants;
import com.pi.client.database.Paths;
import com.pi.client.database.webfiles.GraphicsLoader;
import com.pi.client.debug.EntityMonitorPanel;
import com.pi.client.debug.GraphicsMonitorPanel;
import com.pi.client.def.Definitions;
import com.pi.client.entity.ClientEntityManager;
import com.pi.client.game.MainGame;
import com.pi.client.graphics.RenderLoop;
import com.pi.client.gui.mainmenu.MainMenu;
import com.pi.client.net.ClientNetwork;
import com.pi.client.world.SectorManager;
import com.pi.common.Disposable;
import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;
import com.pi.common.debug.PIResourceViewer;
import com.pi.common.debug.SectorMonitorPanel;
import com.pi.common.debug.ThreadMonitorPanel;
import com.pi.common.game.GameState;
import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.DisplayManager;
import com.pi.gui.GUIKit;

/**
 * The class managing all the client subsystems.
 * <p>
 * The client class contains all the subsystems, and provides an instanced
 * object that allows them to reference each other.
 * 
 * @see com.pi.client.def.Definitions
 * @see com.pi.client.entity.ClientEntityManager
 * @see com.pi.client.net.ClientNetwork
 * @see com.pi.client.world.SectorManager
 * @see com.pi.graphics.device.DisplayManager
 * @author Westin
 * 
 */
public class Client implements Disposable, DeviceRegistration {
	static {
		GUIKit.init();
	}

	/**
	 * The thread group for monitoring all client threads.
	 */
	private ThreadGroup clientThreads;

	/**
	 * The sector manager instance.
	 */
	private SectorManager world;

	/**
	 * The client's entity manager instance.
	 */
	private ClientEntityManager entityManager;

	/**
	 * The definitions manager instance.
	 */
	private Definitions defs;

	/**
	 * Boolean value monitoring the disposal state of the client. Mainly used to
	 * prevent re-disposal of systems.
	 */
	private boolean disposing = false;

	// Network Start
	/**
	 * The client network instance.
	 */
	private ClientNetwork network;
	// Network End

	// Graphics start
	/**
	 * The applet that the client renders on.
	 */
	private Applet cApplet;
	/**
	 * The display manager instance.
	 */
	private DisplayManager displayManager;
	/**
	 * The render loop for rendering the client's screen.
	 */
	private RenderLoop renderLoop;
	// Graphics end

	// GUI Start
	/**
	 * The graphical user interface container for the main game controls.
	 * 
	 * @see com.pi.client.game.MainGame
	 */
	private MainGame mainGame;
	/**
	 * The graphical user interface container for the main menu controls.
	 * 
	 * @see com.pi.client.gui.mainmenu.MainMenu
	 */
	private MainMenu mainMenu;
	/**
	 * Enum for setting the current game state.
	 * 
	 * @see com.pi.common.game.GameState
	 */
	private GameState gameState = GameState.LOADING;
	// GUI End

	// Debug Start
	/**
	 * The logger instance used for logging events.
	 * 
	 * @see com.pi.common.debug.PILogger
	 */
	private final PILogger logger;
	/**
	 * The resource viewer.
	 * <p>
	 * The resource viewer is used to monitor the current state of various game
	 * systems.
	 * 
	 * @see com.pi.common.debug.PIResourceViewer
	 */
	private PIResourceViewer reView;

	// Debug End

	/**
	 * Creates a client on the given applet and the given arguments.
	 * 
	 * Currently the arguments this accepts is a network connection definition
	 * for an IP and port in the format 000.000.000.000:port, or the format
	 * 000.000.000.000, which only defines the IP address.
	 * 
	 * @param applet the applet
	 * @param args the string arguments
	 * 
	 * @return the new client instance
	 */
	public static Client create(final Applet applet,
			final String[] args) {
		String ip = Constants.NETWORK_IP;
		int port = Constants.NETWORK_PORT;
		if (args.length == 1) {
			ip = args[0];
			String[] parts = ip.split(":");
			if (parts.length == 2) {
				ip = parts[0];
				port = Integer.valueOf(parts[1]);
			} else {
				ip = parts[0];
			}
		}
		return new Client(applet, ip, port);
	}

	/**
	 * Creates a instance of the whole game client on the provided applet.
	 * 
	 * @param applet The game container
	 * @param ip the IP address
	 * @param port the port
	 */
	public Client(final Applet applet, final String ip,
			final int port) {
		clientThreads = new ThreadGroup("ClientThreads");
		reView = new PIResourceViewer("Client");
		PILoggerPane plp = new PILoggerPane();
		logger =
				new PILogger(plp.getLogOutput(),
						Paths.getLogFile());
		reView.addTab("Logger", plp);
		reView.addTab("Threads", new ThreadMonitorPanel(
				clientThreads));
		this.cApplet = applet;
		this.renderLoop = new RenderLoop(this);
		this.displayManager =
				new DisplayManager(this, renderLoop);

		// PRE POST INIT
		GraphicsLoader.load(this); // TODO This should be an escapable loop in
		// case of timeouts.

		reView.addTab("Graphics", new GraphicsMonitorPanel(
				displayManager));
		this.world = new SectorManager(this);
		reView.addTab("Sectors", new SectorMonitorPanel(
				this.world));
		this.defs = new Definitions(this);
		network = new ClientNetwork(this, ip, port);
		this.entityManager = new ClientEntityManager(this);
		reView.addTab("Entities", new EntityMonitorPanel(this));

		// Post INIT
		this.displayManager.postInititation();
		this.mainMenu = new MainMenu(this);
		this.mainGame = new MainGame(this);
		gameState = GameState.MAIN_MENU;
	}

	/**
	 * Gets the networking model for this client.
	 * 
	 * @see com.pi.client.net.ClientNetwork
	 * @return the client's network model
	 */
	public final ClientNetwork getNetwork() {
		return network;
	}

	/**
	 * Gets the applet container for this client.
	 * 
	 * @return the container applet
	 */
	public final Applet getApplet() {
		return this.cApplet;
	}

	/**
	 * Gets the display manager for this client.
	 * 
	 * @see com.pi.graphics.device.DisplayManager
	 * @return the display manager instance
	 */
	public final DisplayManager getDisplayManager() {
		return displayManager;
	}

	/**
	 * Gets the sector manager for this client.
	 * 
	 * @see com.pi.client.world.SectorManager
	 * @return the sector manager instance
	 */
	public final SectorManager getWorld() {
		return world;
	}

	@Override
	public final PILogger getLog() {
		return logger;
	}

	@SuppressWarnings("deprecation")
	@Override
	public final void dispose() {
		if (!disposing) {
			disposing = true;
			if (mainGame != null) {
				mainGame.dispose();
			}
			if (displayManager != null) {
				displayManager.dispose();
			}
			if (world != null) {
				world.dispose();
			}
			if (defs != null) {
				defs.dispose();
			}
			if (network != null) {
				network.dispose();
			}
		} else {
			if (reView != null) {
				reView.dispose();
			}

			clientThreads.stop();
			logger.close();
		}
	}

	/**
	 * Gets the current network connection state.
	 * 
	 * @return the connected state of the network
	 */
	public final boolean isNetworkConnected() {
		return getNetwork() != null
				&& getNetwork().isConnected();
	}

	@Override
	public final ThreadGroup getThreadGroup() {
		return clientThreads;
	}

	/**
	 * Gets the client's instance of an entity manager.
	 * 
	 * @see com.pi.client.entity.ClientEntityManager
	 * @return the entity manager instance
	 */
	public final ClientEntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Gets the client's instance of a definitions manager.
	 * 
	 * @see com.pi.client.def.Definitions
	 * @return the definition manager instance
	 */
	public final Definitions getDefs() {
		return defs;
	}

	@Override
	public final void fatalError(final String string) {
		logger.severe(string);
		dispose();
	}

	/**
	 * Gets the current state of the game.
	 * 
	 * @see com.pi.common.game.GameState
	 * @return the current game state
	 */
	public final GameState getGameState() {
		return gameState;
	}

	/**
	 * Gets the main game control container.
	 * 
	 * @see com.pi.client.game.MainGame
	 * @return the main game container instance
	 */
	public final MainGame getMainGame() {
		return mainGame;
	}

	/**
	 * Gets the main menu control container.
	 * 
	 * @see com.pi.client.gui.mainmenu.MainMenu
	 * @return the main menu container instance
	 */
	public final MainMenu getMainMenu() {
		return mainMenu;
	}

	/**
	 * Sets the current game state to the provided state.
	 * 
	 * @param state the new state
	 */
	public final void setGameState(final GameState state) {
		gameState = state;
		if (gameState == GameState.MAIN_GAME) {
			renderLoop.hideAlert();
		}
	}

	@Override
	public final Container getContainer() {
		return getApplet();
	}

	@Override
	public final File getGraphicsFile(final int dir, final int id) {
		return Paths.getGraphicsFile(dir, id);
	}

	/**
	 * Gets the instance of a render loop used to render the client window.
	 * 
	 * @see com.pi.client.graphics.RenderLoop
	 * @return the render loop instance
	 */
	public final RenderLoop getRenderLoop() {
		return renderLoop;
	}
}
