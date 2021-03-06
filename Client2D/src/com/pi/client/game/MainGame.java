package com.pi.client.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.pi.client.Client;
import com.pi.client.graphics.GameRenderLoop;
import com.pi.client.gui.game.MainGameGUI;
import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.Renderable;

/**
 * Displays the main game controls and the game itself.
 * 
 * @author Westin
 * 
 */
public class MainGame implements Renderable, KeyListener,
		MouseListener, MouseMotionListener, MouseWheelListener {
	/**
	 * The client instance.
	 */
	private final Client client;
	/**
	 * The actual game rendering loop.
	 */
	private final GameRenderLoop gameRenderLoop;

	/**
	 * The AWT event processor.
	 */
	private final EventLoop eventLoop;

	/**
	 * The data cache for this client.
	 */
	private final ClientDataCache dataCache =
			new ClientDataCache();

	/**
	 * The container that contains all GUI elements.
	 */
	private final MainGameGUI mainGameGUI;

	/**
	 * Creates the Main Game render loop and controls for the specified client.
	 * 
	 * @param sClient the client instance
	 */
	public MainGame(final Client sClient) {
		this.client = sClient;
		this.gameRenderLoop = new GameRenderLoop(client);
		this.eventLoop = new EventLoop(client);
		this.mainGameGUI = new MainGameGUI(client);
	}

	@Override
	public final void render(final IGraphics g) {
		this.gameRenderLoop.render(g);
		this.mainGameGUI.render(g);
	}

	/**
	 * Disposes of this game's event loop.
	 */
	public final void dispose() {
		if (eventLoop != null) {
			eventLoop.dispose();
		}
	}

	/**
	 * Gets this client's data cache.
	 * 
	 * @return the data cache
	 */
	public final ClientDataCache getClientCache() {
		return dataCache;
	}

	@Override
	public final void mouseWheelMoved(final MouseWheelEvent e) {
		mainGameGUI.mouseWheelMoved(e);
	}

	@Override
	public final void mouseDragged(final MouseEvent e) {
		mainGameGUI.mouseDragged(e);
	}

	@Override
	public final void mouseMoved(final MouseEvent e) {
		mainGameGUI.mouseMoved(e);
	}

	@Override
	public final void mouseClicked(final MouseEvent e) {
		mainGameGUI.mouseClicked(e);
	}

	@Override
	public final void mousePressed(final MouseEvent e) {
		mainGameGUI.mousePressed(e);
	}

	@Override
	public final void mouseReleased(final MouseEvent e) {
		mainGameGUI.mouseReleased(e);
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
		mainGameGUI.mouseEntered(e);
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
		mainGameGUI.mouseExited(e);
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		eventLoop.keyTyped(e);
		mainGameGUI.keyTyped(e);
	}

	@Override
	public final void keyPressed(final KeyEvent e) {
		eventLoop.keyPressed(e);
		mainGameGUI.keyPressed(e);
	}

	@Override
	public final void keyReleased(final KeyEvent e) {
		eventLoop.keyReleased(e);
		mainGameGUI.keyReleased(e);
	}
}
