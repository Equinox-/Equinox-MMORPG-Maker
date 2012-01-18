package com.pi.client.graphics;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.pi.client.Client;
import com.pi.client.graphics.device.IGraphics;
import com.pi.client.gui.LoadingBar;
import com.pi.client.gui.PIPopup;
import com.pi.client.gui.mainmenu.MainMenu;

public class RenderLoop implements Renderable, MouseListener,
	MouseMotionListener, MouseWheelListener, KeyListener {
    private final Client client;
    private final LoadingBar loadBar;
    private MainMenu menu;
    private PIPopup popup = new PIPopup();

    public RenderLoop(Client client) {
	this.client = client;
	this.loadBar = new LoadingBar(client);
	client.getApplet().addMouseListener(popup);
	client.getApplet().addMouseListener(this);
	client.getApplet().addMouseWheelListener(this);
	client.getApplet().addMouseMotionListener(this);
	client.getApplet().addKeyListener(this);
    }

    public void postInitiation() {
	this.menu = new MainMenu(client);
    }

    @Override
    public void render(IGraphics g) {
	if (menu != null && client.getGame() != null) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		menu.render(g);
		break;
	    default:
		client.getGame().render(g);
		break;
	    }
	    // Render popup
	    popup.render(g, client.getApplet().getWidth(), client.getApplet()
		    .getHeight());
	} else
	    loadBar.render(g);
    }

    public MainMenu getMainMenu() {
	return menu;
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
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.keyPressed(e);
		break;
	    }
	}
    }

    @Override
    public void keyReleased(KeyEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.keyReleased(e);
		break;
	    }
	}
    }

    @Override
    public void keyTyped(KeyEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.keyTyped(e);
		break;
	    }
	}
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.mouseWheelMoved(e);
		break;
	    }
	}
    }

    @Override
    public void mouseDragged(MouseEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.mouseDragged(e);
		break;
	    default:
		if (client.getGame() != null)
		    client.getGame().mouseDragged(e);
	    }
	}
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.mouseMoved(e);
		break;
	    default:
		if (client.getGame() != null)
		    client.getGame().mouseMoved(e);
	    }
	}
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.mouseClicked(e);
		break;
	    default:
		if (client.getGame() != null)
		    client.getGame().mouseClicked(e);
	    }
	}
    }

    @Override
    public void mouseEntered(MouseEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.mouseEntered(e);
		break;
	    default:
		if (client.getGame() != null)
		    client.getGame().mouseEntered(e);
	    }
	}
    }

    @Override
    public void mouseExited(MouseEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.mouseExited(e);
		break;
	    default:
		if (client.getGame() != null)
		    client.getGame().mouseExited(e);
	    }
	}
    }

    @Override
    public void mousePressed(MouseEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.mousePressed(e);
		break;
	    default:
		if (client.getGame() != null)
		    client.getGame().mousePressed(e);
	    }
	}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	if (!hasPopup()) {
	    switch (client.gameState) {
	    case MAIN_MENU:
		if (menu != null)
		    menu.mouseReleased(e);
		break;
	    default:
		if (client.getGame() != null)
		    client.getGame().mouseReleased(e);
	    }
	}
    }

    public void hideAlert() {
	popup.setVisible(false);
    }
}
