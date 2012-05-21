package com.pi.client;

import java.applet.Applet;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import com.pi.client.database.Paths;
import com.pi.client.database.webfiles.GraphicsLoader;
import com.pi.client.debug.EntityMonitorPanel;
import com.pi.client.debug.GraphicsMonitorPanel;
import com.pi.client.debug.SectorMonitorPanel;
import com.pi.client.def.Definitions;
import com.pi.client.entity.ClientEntityManager;
import com.pi.client.game.MainGame;
import com.pi.client.graphics.RenderLoop;
import com.pi.client.gui.mainmenu.MainMenu;
import com.pi.client.net.NetClient;
import com.pi.client.world.World;
import com.pi.common.Disposable;
import com.pi.common.debug.PILogger;
import com.pi.common.debug.PILoggerPane;
import com.pi.common.debug.PIResourceViewer;
import com.pi.common.debug.ThreadMonitorPanel;
import com.pi.common.game.GameState;
import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.DisplayManager;
import com.pi.gui.GUIKit;

public class Client implements Disposable, DeviceRegistration {
	static {
		GUIKit.init();
	}
	private ThreadGroup clientThreads;

	private World world;
	private ClientEntityManager entityManager;
	private Definitions defs;
	private boolean disposing = false;

	// Network Start
	private String ip = "127.0.0.1";
	private int port = 9999;
	private NetClient network;
	// Network End

	// Graphics start
	private Applet cApplet;
	private DisplayManager displayManager;
	private RenderLoop renderLoop;
	// Graphics end

	// GUI Start
	private MainGame mainGame;
	private MainMenu mainMenu;
	private GameState gameState = GameState.LOADING;
	// GUI End

	// Debug Start
	private final PILogger logger;
	private PIResourceViewer reView;

	// Debug End

	public Client(Applet applet) {

		/*
		 * ip = JOptionPane.showInputDialog(applet,
		 * "Please input the ip: (xxx.xxx.xxx.xxx)"); while (true) { try { port
		 * = Integer.valueOf(JOptionPane.showInputDialog(applet,
		 * "Please input the port")); break; } catch (NumberFormatException e) {
		 * JOptionPane.showMessageDialog(applet,
		 * "Bad number format, try again"); } }
		 */

		clientThreads = new ThreadGroup("ClientThreads");
		reView = new PIResourceViewer("Client");
		reView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		reView.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});
		PILoggerPane plp = new PILoggerPane();
		logger = new PILogger(Paths.getLogFile(), plp.logOut);
		reView.addTab("Logger", plp);
		reView.addTab("Threads", new ThreadMonitorPanel(clientThreads));
		this.cApplet = applet;
		this.displayManager = new DisplayManager(this,
				renderLoop = new RenderLoop(this));

		// PRE POST INIT
		GraphicsLoader.load(this);

		reView.addTab("Graphics", new GraphicsMonitorPanel(this.displayManager));
		this.world = new World(this);
		reView.addTab("Sectors",
				new SectorMonitorPanel(this.world.getSectorManager()));
		this.defs = new Definitions(this);
		network = new NetClient(this, ip, port);
		this.entityManager = new ClientEntityManager(this);
		reView.addTab("Entities", new EntityMonitorPanel(entityManager));

		// Post INIT
		this.displayManager.postInititation();
		this.mainMenu = new MainMenu(this);
		this.mainGame = new MainGame(this);
		gameState = GameState.MAIN_MENU;
	}

	public NetClient getNetwork() {
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

	@Override
	public PILogger getLog() {
		return logger;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void dispose() {
		if (!disposing) {
			disposing = true;
			if (displayManager != null)
				displayManager.dispose();
			if (world != null)
				world.dispose();
			if (defs != null)
				defs.dispose();
			if (network != null)
				network.dispose();
		} else {
			if (reView != null)
				reView.dispose();

			clientThreads.stop();
			logger.close();
		}
	}

	public boolean isNetworkConnected() {
		return getNetwork() != null && getNetwork().isConnected();
	}

	public String getNetworkIP() {
		return ip;
	}

	public int getNetworkPort() {
		return port;
	}

	@Override
	public ThreadGroup getThreadGroup() {
		return clientThreads;
	}

	public ClientEntityManager getEntityManager() {
		return entityManager;
	}

	public Definitions getDefs() {
		return defs;
	}

	@Override
	public void fatalError(String string) {
		logger.severe(string);
		dispose();
	}

	public GameState getGameState() {
		return gameState;
	}

	public MainGame getMainGame() {
		return mainGame;
	}

	public MainMenu getMainMenu() {
		return mainMenu;
	}

	public void setGameState(GameState state) {
		gameState = state;
		if (gameState == GameState.MAIN_GAME)
			renderLoop.hideAlert();
	}

	@Override
	public Container getContainer() {
		return getApplet();
	}

	@Override
	public File getGraphicsFile(int id) {
		return Paths.getGraphicsFile(id);
	}

	public RenderLoop getRenderLoop() {
		return renderLoop;
	}
}
