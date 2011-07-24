package com.pi.client.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

import com.pi.client.graphics.Renderable;
import com.pi.client.graphics.device.IGraphics;

public class PIComponent implements Renderable, MouseWheelListener,
	MouseListener, MouseMotionListener, KeyListener {
    protected Map<PIStyle.StyleType, PIStyle> styles = new HashMap<PIStyle.StyleType, PIStyle>();
    protected int x, y, width, height;
    protected String content = "";
    protected PIContainer parent;
    protected boolean hovering = false;
    protected List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
    protected List<MouseWheelListener> mouseWheelListeners = new ArrayList<MouseWheelListener>();
    protected List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
    protected List<KeyListener> keyListeners = new ArrayList<KeyListener>();
    protected boolean isVisible = true;
    protected boolean isFocused = false;
    protected boolean isActive = false;
    protected boolean clipContents = false;

    public PIComponent() {
	styles.put(PIStyle.StyleType.Normal, GUIKit.defaultStyle);
    }

    @Override
    public void render(IGraphics g) {
	update();
	paintBackground(g);
	paintForeground(g);
    }

    public PIStyle getStyle(PIStyle.StyleType type) {
	if (!styles.containsKey(type))
	    styles.put(type, new PIStyle());
	return styles.get(type);
    }

    public void setStyle(PIStyle.StyleType type, PIStyle style) {
	styles.put(type, style);
    }

    public void update() {

    }

    public PIStyle getCurrentStyle() {
	if (hovering && styles.containsKey(PIStyle.StyleType.Hover))
	    return styles.get(PIStyle.StyleType.Hover);
	if (isActive && styles.containsKey(PIStyle.StyleType.Active))
	    return styles.get(PIStyle.StyleType.Active);
	return styles.get(PIStyle.StyleType.Normal);
    }

    public void setActive(boolean active) {
	this.isActive = active;
    }

    public void paintBackground(IGraphics g) {
	PIStyle style;
	if (isVisible && (style = getCurrentStyle()) != null) {
	    Rectangle bounds = getAbsoluteBounds();
	    if (style.background != null) {
		g.setColor(style.background);
		g.fillRect(bounds);
	    }
	    if (style.bgImage != null) {
		g.drawTiledImage(style.bgImage, bounds,
			style.stretchBackgroundX, style.stretchBackgroundY);
	    }
	    if (style.border != null) {
		g.setColor(style.border);
		g.drawRect(bounds);
	    }
	}
    }

    public void setStyleSet(PIStyle.PIStyleSet set, boolean clones) {
	if (clones) {
	    if (set.active != null)
		styles.put(PIStyle.StyleType.Active, set.active.clone());
	    if (set.hover != null)
		styles.put(PIStyle.StyleType.Hover, set.hover.clone());
	    styles.put(PIStyle.StyleType.Normal, set.normal.clone());
	} else {
	    if (set.active != null)
		styles.put(PIStyle.StyleType.Active, set.active);
	    if (set.hover != null)
		styles.put(PIStyle.StyleType.Hover, set.hover);
	    styles.put(PIStyle.StyleType.Normal, set.normal);
	}
    }

    public void paintForeground(IGraphics g) {
	PIStyle style;
	if (isVisible && (style = getCurrentStyle()) != null) {
	    String disp;
	    Rectangle bounds = getAbsolutePaddedBounds();
	    Rectangle clipArea = null;
	    if (clipContents) {
		clipArea = (Rectangle) g.getClip().clone();
		g.setClip(bounds);
	    }
	    if (style.foreground != null && style.font != null
		    && content != null
		    && (disp = getDisplay()).trim().length() > 0) {
		g.drawWrappedText(bounds, style.font, disp, style.foreground,
			style.hAlign, style.vAlign);
	    }
	    if (clipContents) {
		g.setClip(clipArea);
	    }
	}
    }

    protected Rectangle getAbsolutePaddedBounds() {
	return getAbsoluteBounds();
    }

    public boolean isVisible() {
	return isVisible;
    }

    public void setVisible(boolean visible) {
	this.isVisible = visible;
    }

    public PIContainer getParent() {
	return parent;
    }

    public void setParent(PIContainer parent) {
	this.parent = parent;
    }

    public String getContent() {
	return content;
    }

    public String getDisplay() {
	return content;
    }

    public int getAbsoluteX() {
	return (parent != null ? parent.getAbsoluteX() : 0) + x;
    }

    public int getAbsoluteY() {
	return (parent != null ? parent.getAbsoluteX() : 0) + y;
    }

    public Rectangle getAbsoluteBounds(Rectangle r) {
	r.setBounds(getAbsoluteX(), getAbsoluteY(), width, height);
	return r;
    }

    public Rectangle getAbsoluteBounds() {
	return getAbsoluteBounds(new Rectangle());
    }

    public int getX() {
	return x;
    }

    public int getY() {
	return y;
    }

    public int getWidth() {
	return width;
    }

    public int getHeight() {
	return height;
    }

    public void setContent(String s) {
	this.content = s;
    }

    public void setSize(int width, int height) {
	this.width = width;
	this.height = height;
    }

    public void setSize(Dimension d) {
	this.width = d.width;
	this.height = d.height;
    }

    public void setWidth(int width) {
	this.width = width;
    }

    public void setHeight(int height) {
	this.height = height;
    }

    public void setX(int x) {
	this.x = x;
    }

    public void setY(int y) {
	this.y = y;
    }

    public void setLocation(int x, int y) {
	this.x = x;
	this.y = y;
    }

    public void setLocation(Point2D p) {
	this.x = (int) p.getX();
	this.y = (int) p.getY();
    }

    public void setFocused(boolean value) {
	this.isFocused = value;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	Rectangle r = getAbsoluteBounds();
	if (isFocused = r.contains(e.getPoint())) {
	    MouseEvent nE = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - r.x, e.getY()
			    - r.y, e.getClickCount(), e.isPopupTrigger(),
		    e.getButton());
	    for (MouseListener l : mouseListeners)
		l.mouseClicked(nE);
	}
    }

    @Override
    public void mousePressed(MouseEvent e) {
	Rectangle r = getAbsoluteBounds();
	if (r.contains(e.getPoint())) {
	    MouseEvent nE = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - r.x, e.getY()
			    - r.y, e.getClickCount(), e.isPopupTrigger(),
		    e.getButton());
	    for (MouseListener l : mouseListeners)
		l.mousePressed(nE);
	    isFocused = true;
	} else {
	    isFocused = false;
	}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	Rectangle r = getAbsoluteBounds();
	if (r.contains(e.getPoint())) {
	    MouseEvent nE = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - r.x, e.getY()
			    - r.y, e.getClickCount(), e.isPopupTrigger(),
		    e.getButton());
	    for (MouseListener l : mouseListeners)
		l.mouseReleased(nE);
	}
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	Rectangle r = getAbsoluteBounds();
	if (r.contains(e.getPoint())) {
	    MouseEvent nE = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - r.x, e.getY()
			    - r.y, e.getClickCount(), e.isPopupTrigger(),
		    e.getButton());
	    for (MouseMotionListener l : mouseMotionListeners)
		l.mouseMoved(nE);
	    hovering = true;
	} else {
	    hovering = false;
	}
    }

    @Override
    public void mouseDragged(MouseEvent e) {
	Rectangle r = getAbsoluteBounds();
	if (r.contains(e.getPoint())) {
	    MouseEvent nE = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - r.x, e.getY()
			    - r.y, e.getClickCount(), e.isPopupTrigger(),
		    e.getButton());
	    for (MouseMotionListener l : mouseMotionListeners)
		l.mouseDragged(nE);
	}
    }

    public void addMouseMotionListener(MouseMotionListener l) {
	mouseMotionListeners.add(l);
    }

    public void addMouseListener(MouseListener l) {
	mouseListeners.add(l);
    }

    @Override
    public void keyPressed(KeyEvent e) {
	if (isFocused)
	    for (KeyListener l : keyListeners)
		l.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
	if (isFocused)
	    for (KeyListener l : keyListeners)
		l.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
	if (isFocused)
	    for (KeyListener l : keyListeners)
		l.keyTyped(e);
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent arg0) {
	if (hovering) {
	}
    }

    public void setClipContents(boolean val) {
	this.clipContents = val;
    }

    public boolean doesClipContents() {
	return this.clipContents;
    }
}
