package com.pi.client.graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.pi.client.Client;
import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.Renderable;
import com.pi.gui.LoadingBar;
import com.pi.gui.PIPopup;

public class RenderLoop implements Renderable, MouseListener,
		MouseMotionListener, MouseWheelListener, KeyListener {
	private final Client client;
	private final LoadingBar loadBar;
	private PIPopup popup = new PIPopup();

	public RenderLoop(Client client) {
		this.client = client;
		this.loadBar = new LoadingBar(client);
		client.getApplet().addMouseListener(this);
		client.getApplet().addMouseWheelListener(this);
		client.getApplet().addMouseMotionListener(this);
		client.getApplet().addKeyListener(this);
	}

	@Override
	public void render(IGraphics g) {
		if (client.getMainMenu() != null && client.getMainGame() != null) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				client.getMainMenu().render(g);
				break;
			case MAIN_GAME:
				client.getMainGame().render(g);
				break;
			}
			// Render popup
			popup.render(g, client.getApplet().getWidth(), client.getApplet()
					.getHeight());
		} else
			loadBar.render(g);
	}

	public void alert(String message) {
		popup.setVisible(true);
		popup.displayLoadingBar(false);
		popup.setContent(message);
		popup.setCloseable(true);
	}

	public void loading(String message) {
		popup.setVisible(true);
		popup.setContent(message);
		popup.setCloseable(false);
		popup.displayLoadingBar(true);
	}

	public boolean hasPopup() {
		return popup.isVisible();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().keyPressed(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().keyPressed(e);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().keyReleased(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().keyReleased(e);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().keyTyped(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().keyTyped(e);
			}
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().mouseWheelMoved(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().mouseWheelMoved(e);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().mouseDragged(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().mouseDragged(e);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().mouseMoved(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().mouseMoved(e);
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().mouseClicked(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().mouseClicked(e);
			}
		}else{
			popup.mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().mouseEntered(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().mouseEntered(e);
			}
		}else{
			popup.mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().mouseExited(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().mouseExited(e);
			}
		}else{
			popup.mouseExited(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().mousePressed(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().mousePressed(e);
			}
		}else{
			popup.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null)
					client.getMainMenu().mouseReleased(e);
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null)
					client.getMainGame().mouseReleased(e);
			}
		} else {
			popup.mouseReleased(e);
		}
	}

	public void hideAlert() {
		popup.setVisible(false);
	}
}
