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

/**
 * The main render loop controlling all the other rendering processes.
 * 
 * @author Westin
 * 
 */
public class RenderLoop implements Renderable, MouseListener,
		MouseMotionListener, MouseWheelListener, KeyListener {
	/**
	 * The client instance that this render loop is bound to.
	 */
	private final Client client;
	/**
	 * The loading bar displayed until all the client initilization is complete.
	 */
	private final LoadingBar loadBar;
	/**
	 * The popup container showing any notifications.
	 */
	private PIPopup popup = new PIPopup();

	/**
	 * Creates and registers this render loop with the client.
	 * 
	 * @param sClient the client to bind to.
	 */
	public RenderLoop(final Client sClient) {
		this.client = sClient;
		this.loadBar = new LoadingBar(client);
		client.getApplet().addMouseListener(this);
		client.getApplet().addMouseWheelListener(this);
		client.getApplet().addMouseMotionListener(this);
		client.getApplet().addKeyListener(this);
	}

	@Override
	public final void render(final IGraphics g) {
		if (client.getMainMenu() != null
				&& client.getMainGame() != null) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				client.getMainMenu().render(g);
				break;
			case MAIN_GAME:
				client.getMainGame().render(g);
				break;
			default:
				break;
			}
			// Render popup
			popup.render(g, 0, 0, client.getApplet().getWidth(),
					client.getApplet().getHeight());
			return;
		}
		loadBar.render(g);
	}

	/**
	 * Shows an alert with the popup container.
	 * 
	 * @see com.pi.gui.PIPopup
	 * @param message the message to show
	 */
	public final void alert(final String message) {
		popup.setVisible(true);
		popup.displayLoadingBar(false);
		popup.setContent(message);
		popup.setCloseable(true);
	}

	/**
	 * Shows a loading bar with the popup container.
	 * 
	 * @see com.pi.gui.PIPopup
	 * @param message the loading message
	 */
	public final void loading(final String message) {
		popup.setVisible(true);
		popup.setContent(message);
		popup.setCloseable(false);
		popup.displayLoadingBar(true);
	}

	/**
	 * Indicates the current state of the popup.
	 * 
	 * @return if the popup is visible
	 */
	public final boolean hasPopup() {
		return popup.isVisible();
	}

	@Override
	public final void keyPressed(final KeyEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().keyPressed(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().keyPressed(e);
				}
			default:
				break;
			}
		}
	}

	@Override
	public final void keyReleased(final KeyEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().keyReleased(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().keyReleased(e);
				}
			default:
				break;
			}
		}
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().keyTyped(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().keyTyped(e);
				}
			default:
				break;
			}
		}
	}

	@Override
	public final void mouseWheelMoved(final MouseWheelEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().mouseWheelMoved(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().mouseWheelMoved(e);
				}
			default:
				break;
			}
		}
	}

	@Override
	public final void mouseDragged(final MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().mouseDragged(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().mouseDragged(e);
				}
			default:
				break;
			}
		}
	}

	@Override
	public final void mouseMoved(final MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().mouseMoved(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().mouseMoved(e);
				}
			default:
				break;
			}
		}
	}

	@Override
	public final void mouseClicked(final MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().mouseClicked(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().mouseClicked(e);
				}
			default:
				break;
			}
		} else {
			popup.mouseClicked(e);
		}
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().mouseEntered(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().mouseEntered(e);
				}
			default:
				break;
			}
		} else {
			popup.mouseEntered(e);
		}
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().mouseExited(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().mouseExited(e);
				}
			default:
				break;
			}
		} else {
			popup.mouseExited(e);
		}
	}

	@Override
	public final void mousePressed(final MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().mousePressed(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().mousePressed(e);
				}
			default:
				break;
			}
		} else {
			popup.mousePressed(e);
		}
	}

	@Override
	public final void mouseReleased(final MouseEvent e) {
		if (!hasPopup()) {
			switch (client.getGameState()) {
			case MAIN_MENU:
				if (client.getMainMenu() != null) {
					client.getMainMenu().mouseReleased(e);
				}
				break;
			case MAIN_GAME:
				if (client.getMainGame() != null) {
					client.getMainGame().mouseReleased(e);
				}
			default:
				break;
			}
		} else {
			popup.mouseReleased(e);
		}
	}

	/**
	 * Hides the current popup.
	 */
	public final void hideAlert() {
		popup.setVisible(false);
	}
}
