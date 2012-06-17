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

public class MainGame implements Renderable, KeyListener, MouseListener,
		MouseMotionListener, MouseWheelListener {
	private final Client client;
	private final GameRenderLoop gameRenderLoop;

	public MainGame(final Client client) {
		this.client = client;
		this.gameRenderLoop = new GameRenderLoop(client);
	}

	@Override
	public void render(IGraphics g) {
		this.gameRenderLoop.render(g);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		Entity ent = client.getEntityManager().getLocalEntity();
		if (ent != null) {
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
			}
			if (dir != null) {
				ClientEntity local = client.getEntityManager().getLocalEntity();
				if (local != null && !local.isMoving()) {
					local.setDir(dir);
					if (local.canMove(client.getWorld().getSectorManager())) {
						local.doMovement();
						client.getNetwork().send(
								Packet14ClientMove.create(local));
					}
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
