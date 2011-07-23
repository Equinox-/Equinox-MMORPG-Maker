package com.pi.client.gui;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import com.pi.client.graphics.device.IGraphics;

public class PIContainer extends PIComponent {
    protected List<PIComponent> children = new ArrayList<PIComponent>();

    public void add(PIComponent child) {
	children.add(child);
	child.setParent(this);
    }

    public List<PIComponent> getChildren() {
	return children;
    }

    @Override
    public void render(IGraphics g) {
	super.render(g);
	for (PIComponent child : children)
	    child.render(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	super.mouseClicked(e);
	for (PIComponent child : children)
	    child.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
	super.mousePressed(e);
	for (PIComponent child : children)
	    child.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	super.mouseReleased(e);
	for (PIComponent child : children)
	    child.mouseReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	super.mouseMoved(e);
	for (PIComponent child : children)
	    child.mouseMoved(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
	super.mouseDragged(e);
	for (PIComponent child : children)
	    child.mouseDragged(e);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
	super.mouseWheelMoved(e);
	for (PIComponent child : children)
	    child.mouseWheelMoved(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
	super.keyReleased(e);
	for (PIComponent child : children)
	    child.keyReleased(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
	super.keyPressed(e);
	for (PIComponent child : children)
	    child.keyPressed(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
	super.keyTyped(e);
	for (PIComponent child : children)
	    child.keyTyped(e);
    }
}
