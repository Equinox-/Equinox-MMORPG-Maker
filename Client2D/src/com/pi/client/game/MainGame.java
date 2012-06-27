package com.pi.client.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.pi.client.Client;
import com.pi.client.entity.ClientEntity;
import com.pi.client.graphics.GameRenderLoop;
import com.pi.common.contants.Direction;
import com.pi.common.game.Entity;
import com.pi.common.net.packet.Packet14ClientMove;
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
	 * If this client is running.
	 */
	private boolean isRunning = false;

	/**
	 * Creates the Main Game render loop and controls for the specified client.
	 * 
	 * @param sClient the client instance
	 */
	public MainGame(final Client sClient) {
		this.client = sClient;
		this.gameRenderLoop = new GameRenderLoop(client);
	}

	@Override
	public final void render(final IGraphics g) {
		this.gameRenderLoop.render(g);
	}

	@Override
	public final void keyPressed(final KeyEvent e) {
		ClientEntity cEnt =
				client.getEntityManager().getLocalEntity();
		if (cEnt != null) {
			Direction dir = null;
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				dir = Direction.UP;
				break;
			case KeyEvent.VK_DOWN:
				dir = Direction.DOWN;
				break;
			case KeyEvent.VK_LEFT:
				dir = Direction.LEFT;
				break;
			case KeyEvent.VK_RIGHT:
				dir = Direction.RIGHT;
				break;
			default:
				return;
			}
			if (!cEnt.isMoving()) {
				Entity local = cEnt.getWrappedEntity();
				local.setDir(dir);
				if (local.canMove(client.getWorld())) {
					cEnt.doMovement(isRunning);
					client.getNetwork().send(
							Packet14ClientMove.create(local));
				}
			}
		}
	}

	@Override
	public final void keyReleased(final KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SHIFT:
			isRunning = !isRunning;
		default:
			break;
		}
	}

	@Override
	public void keyTyped(final KeyEvent arg0) {
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent arg0) {
	}

	@Override
	public void mouseDragged(final MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(final MouseEvent arg0) {
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
	}

	@Override
	public void mousePressed(final MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
	}
}
