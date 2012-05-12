package com.pi.gui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import com.pi.graphics.device.IGraphics;

public class PIContainer extends PIComponent {
    protected List<PIComponent> children = new ArrayList<PIComponent>();
    protected List<Integer> tabIndex = new ArrayList<Integer>();

    public void add(PIComponent child) {
	children.add(child);
	child.setParent(this);
    }

    public List<PIComponent> getChildren() {
	return children;
    }

    @Override
    public void compile() {
	super.compile();
	for (PIComponent p : children) {
	    p.compile();
	}
    }

    public void setTabIndex(PIComponent p, int idx) {
	int inIDX = children.indexOf(p);
	if (inIDX != -1) {
	    int tCIDX = tabIndex.indexOf(inIDX);
	    if (tCIDX != -1)
		tabIndex.remove(tCIDX);
	    if (idx >= tabIndex.size())
		tabIndex.add(inIDX);
	    else if (idx >= 0)
		tabIndex.add(idx, inIDX);
	}
    }

    @Override
    public void render(IGraphics g) {
	super.render(g);
	if (isVisible) {
	    Rectangle currentBounds = null;
	    Rectangle bounds = getAbsolutePaddedBounds();
	    if (clipContents)
		currentBounds = (Rectangle) g.getClip().clone();
	    for (PIComponent child : children) {
		if (clipContents)
		    g.setClip(bounds);
		child.render(g);
	    }
	    if (clipContents)
		g.setClip(currentBounds);
	}
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
	boolean consumed = false;

	if (e.getKeyChar() == '\t') {
	    int newIDX = getCurrentTabIndex() + 1;
	    if (newIDX >= tabIndex.size())
		newIDX = 0;
	    if (newIDX < tabIndex.size()) {
		for (PIComponent c : children)
		    c.setFocused(false);
		PIComponent c = children.get(tabIndex.get(newIDX));
		if (c != null) {
		    c.setFocused(true);
		    consumed = true;
		}
	    }
	}
	if (!consumed) {
	    super.keyTyped(e);
	    for (PIComponent child : children) {
		child.keyTyped(e);
	    }
	}
    }

    public int getCurrentTabIndex() {
	int idx = -1;
	for (int i = 0; i < children.size(); i++) {
	    PIComponent c = children.get(i);
	    if (c.isFocused) {
		int nI = tabIndex.indexOf(i);
		if (nI != -1)
		    idx = nI;
	    }
	}
	return idx;
    }
}
