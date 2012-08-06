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
	 * Creates the Main Game render loop and controls for the specified client.
	 * 
	 * @param sClient the client instance
	 */
	public MainGame(final Client sClient) {
		this.client = sClient;
		this.gameRenderLoop = new GameRenderLoop(client);
		this.eventLoop = new EventLoop(sClient);
	}

	@Override
	public final void render(final IGraphics g) {
		this.gameRenderLoop.render(g);
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
	}

	@Override
	public final void mouseDragged(final MouseEvent e) {
	}

	@Override
	public final void mouseMoved(final MouseEvent e) {
	}

	@Override
	public final void mouseClicked(final MouseEvent e) {
	}

	@Override
	public final void mousePressed(final MouseEvent e) {
	}

	@Override
	public final void mouseReleased(final MouseEvent e) {
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		eventLoop.keyTyped(e);
	}

	@Override
	public final void keyPressed(final KeyEvent e) {
		eventLoop.keyPressed(e);
	}

	@Override
	public final void keyReleased(final KeyEvent e) {
		eventLoop.keyReleased(e);
	}
}
