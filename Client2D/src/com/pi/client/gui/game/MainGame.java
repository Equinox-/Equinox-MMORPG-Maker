package com.pi.client.gui.game;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.pi.client.Client;
import com.pi.client.graphics.GameRenderLoop;
import com.pi.client.graphics.Renderable;
import com.pi.client.graphics.device.IGraphics;
import com.pi.common.database.Location;
import com.pi.common.game.Entity;
import com.pi.common.net.packet.Packet14ClientMove;

public class MainGame implements Renderable, KeyListener, MouseListener,
	MouseMotionListener, MouseWheelListener {
    private final static boolean localUpdate = false;
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

    public void registerToApplet() {
	Component c = client.getDisplayManager().getListenerRegistration();
	c.addMouseListener(this);
	c.addKeyListener(this);
	c.addMouseMotionListener(this);
	c.addMouseWheelListener(this);
    }

    public void unregisterFromApplet() {
	Component c = client.getDisplayManager().getListenerRegistration();
	c.removeMouseListener(this);
	c.removeKeyListener(this);
	c.removeMouseMotionListener(this);
	c.removeMouseWheelListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
	Entity ent = client.getEntityManager().getLocalEntity();
	if (ent != null) {
	    System.out.println("Pressed: " + e.getKeyCode());
	    if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
		Location nL = new Location(ent.x + 1, ent.plane, ent.z);
		client.getNetwork().send(Packet14ClientMove.create(nL));
		if (localUpdate && client.getEntityManager().getLocalEntity() != null)
		    client.getEntityManager().getLocalEntity().setLocation(nL);
	    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
		Location nL = new Location(ent.x - 1, ent.plane, ent.z);
		client.getNetwork().send(Packet14ClientMove.create(nL));
		if (localUpdate && client.getEntityManager().getLocalEntity() != null)
		    client.getEntityManager().getLocalEntity().setLocation(nL);
	    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
		Location nL = new Location(ent.x, ent.plane, ent.z - 1);
		client.getNetwork().send(Packet14ClientMove.create(nL));
		if (localUpdate && client.getEntityManager().getLocalEntity() != null)
		    client.getEntityManager().getLocalEntity().setLocation(nL);
	    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
		Location nL = new Location(ent.x, ent.plane, ent.z + 1);
		client.getNetwork().send(Packet14ClientMove.create(nL));
		if (localUpdate && client.getEntityManager().getLocalEntity() != null)
		    client.getEntityManager().getLocalEntity().setLocation(nL);
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
