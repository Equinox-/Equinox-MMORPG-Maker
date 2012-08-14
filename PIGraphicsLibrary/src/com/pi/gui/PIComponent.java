package com.pi.gui;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.Renderable;
import com.pi.gui.PIStyle.StyleType;

/**
 * An extensible GUI component with lots of the configurable options, mainly in
 * the {@link PIStyle} class.
 * 
 * @author Westin
 * 
 */
public class PIComponent implements Renderable,
		MouseWheelListener, MouseListener, MouseMotionListener,
		KeyListener {
	/**
	 * The current style mapping for each different state.
	 */
	private PIStyle[] styles = new PIStyle[PIStyle.StyleType
			.values().length];
	/**
	 * The relative x, y position of this component.
	 */
	private int x, y;
	/**
	 * The size of this component.
	 */
	private int width, height;
	/**
	 * The text content of this component.
	 */
	private String content = "";
	/**
	 * The parent component or <code>null</code> if none exists.
	 */
	private PIContainer parent;
	/**
	 * If this component is has the cursor above it.
	 */
	private boolean hovering = false;
	/**
	 * The mouse listeners registered to this component.
	 */
	private List<MouseListener> mouseListeners =
			new ArrayList<MouseListener>();
	/**
	 * The mouse wheel listeners registered to this component.
	 */
	private List<MouseWheelListener> mouseWheelListeners =
			new ArrayList<MouseWheelListener>();
	/**
	 * The mouse motion listeners registered to this component.
	 */
	private List<MouseMotionListener> mouseMotionListeners =
			new ArrayList<MouseMotionListener>();
	/**
	 * The key listeners registered to this component.
	 */
	private List<KeyListener> keyListeners =
			new ArrayList<KeyListener>();
	/**
	 * If this component is visible.
	 */
	private boolean isVisible = true;
	/**
	 * This component has focus. (Was clicked on, and the mouse hasn't clicked
	 * anywhere else recently.)
	 */
	private boolean isFocused = false;
	/**
	 * General purpose is active flag.
	 */
	private boolean isActive = false;

	/**
	 * If the contents of this component should be clipped.
	 */
	private boolean clipContents = false;
	/**
	 * The compiled absolute x and y position.
	 */
	private int absX, absY;

	/**
	 * Creates a new PIComponent with the {@link GUIKit#DEFAULT_STYLE} for
	 * components.
	 */
	public PIComponent() {
		styles[PIStyle.StyleType.NORMAL.ordinal()] =
				GUIKit.DEFAULT_STYLE;
	}

	@Override
	public void render(final IGraphics g) {
		update();
		paintBackground(g);
		paintForeground(g);
	}

	/**
	 * Called each time the component is rendered.
	 */
	public void update() {
	}

	/**
	 * Gets the style bound to the specified style type. This will never return
	 * <code>null</code>, as if the mapping is <code>null</code>, it will be set
	 * to a clone of the {@link GUIKit#DEFAULT_STYLE}.
	 * 
	 * @param type the style type
	 * @return the style instance
	 */
	public final PIStyle getStyle(final PIStyle.StyleType type) {
		if (styles[type.ordinal()] == null) {
			styles[type.ordinal()] =
					GUIKit.DEFAULT_STYLE.clone();
		}
		return styles[type.ordinal()];
	}

	/**
	 * Sets the style bound to the specified type.
	 * 
	 * @param type the style type
	 * @param style the style
	 */
	public final void setStyle(final PIStyle.StyleType type,
			final PIStyle style) {
		styles[type.ordinal()] = style;
	}

	/**
	 * Updates the absolute x and y position of this component. Called every
	 * time the x and y position of this component, or any parent components
	 * changes.
	 */
	public void compile() {
		if (parent != null) {
			absX = parent.getAbsoluteX() + x;
			absY = parent.getAbsoluteY() + y;
		} else {
			absX = x;
			absY = y;
		}
	}

	/**
	 * Checks if the style mapping has a non-null entry for the given style
	 * type.
	 * 
	 * @param type the style type
	 * @return if the mapping is non-null
	 */
	public final boolean containsStyle(
			final PIStyle.StyleType type) {
		return styles[type.ordinal()] != null;
	}

	/**
	 * Gets the currently active style for this component.
	 * 
	 * @return the current style
	 */
	public PIStyle getCurrentStyle() {
		if (hovering && containsStyle(PIStyle.StyleType.HOVER)) {
			return styles[PIStyle.StyleType.HOVER.ordinal()];
		}
		if ((isFocused || isActive())
				&& containsStyle(PIStyle.StyleType.ACTIVE)) {
			return styles[PIStyle.StyleType.ACTIVE.ordinal()];
		}
		return styles[PIStyle.StyleType.NORMAL.ordinal()];
	}

	/**
	 * Sets the active state of this component.
	 * 
	 * @param active the new active state
	 */
	public final void setActive(final boolean active) {
		this.isActive = active;
	}

	/**
	 * Paints the background of this component to the provided graphics object.
	 * 
	 * @param g the graphics object
	 */
	public void paintBackground(final IGraphics g) {
		if (isVisible) {
			PIStyle style = getCurrentStyle();
			if (style == null) {
				return;
			}
			Rectangle bounds = getAbsoluteBounds();
			if (style.background != null) {
				g.setColor(style.background);
				g.fillRect(bounds);
			}
			if (style.bgImage != null) {
				g.drawTiledImage(style.bgImage, bounds,
						style.stretchBackgroundX,
						style.stretchBackgroundY);
			}
			if (style.border != null) {
				g.setColor(style.border);
				g.drawRect(bounds);
			}
		}
	}

	/**
	 * Sets the current styles to this style set's styles. Cloning them if
	 * specified.
	 * 
	 * @param set the style set
	 * @param clones if the styles should be cloned
	 */
	public final void setStyleSet(final PIStyle.PIStyleSet set,
			final boolean clones) {
		if (clones) {
			for (StyleType type : StyleType.values()) {
				if (set.getStyle(type) != null) {
					setStyle(type, set.getStyle(type).clone());
				}
			}
		} else {
			for (StyleType type : StyleType.values()) {
				if (set.getStyle(type) != null) {
					setStyle(type, set.getStyle(type));
				}
			}
		}
	}

	/**
	 * Paints the foreground of this component to the specified graphics object.
	 * 
	 * @param g the graphics object
	 */
	public final void paintForeground(final IGraphics g) {
		if (isVisible) {
			PIStyle style = getCurrentStyle();
			if (style == null) {
				return;
			}
			Rectangle bounds = getAbsolutePaddedBounds();
			Rectangle clipArea = null;
			if (clipContents) {
				clipArea = (Rectangle) g.getClip().clone();
				g.setClip(bounds);
			}
			if (style.foreground != null && style.font != null
					&& content != null) {
				String disp = getDisplay().trim();
				if (disp.length() > 0) {
					g.drawWrappedText(bounds, style.font, disp,
							style.foreground, style.hAlign,
							style.vAlign);
				}
			}
			if (clipContents) {
				g.setClip(clipArea);
			}
		}
	}

	/**
	 * Gets the absolute padded bounds of this component.
	 * 
	 * @return the bounds rectangle
	 */
	protected Rectangle getAbsolutePaddedBounds() {
		return getAbsoluteBounds();
	}

	/**
	 * Checks if this component if visible.
	 * 
	 * @return if the component is visible
	 */
	public final boolean isVisible() {
		return isVisible;
	}

	/**
	 * @return the isFocused
	 */
	public final boolean isFocused() {
		return isFocused;
	}

	/**
	 * Sets the visible flag of this component.
	 * 
	 * @param visible the new visible state
	 */
	public final void setVisible(final boolean visible) {
		this.isVisible = visible;
	}

	/**
	 * Gets the parent container of this component, or <code>null</code> if none
	 * exists.
	 * 
	 * @return the parent
	 */
	public final PIContainer getParent() {
		return parent;
	}

	/**
	 * Sets the parent container of this component, or <code>null</code> if it a
	 * top level component.
	 * 
	 * @param sParent the new parent
	 */
	public final void setParent(final PIContainer sParent) {
		this.parent = sParent;
	}

	/**
	 * Gets the text content of this component.
	 * 
	 * @return the text content
	 */
	public final String getContent() {
		return content;
	}

	/**
	 * The displayed text for this component.
	 * 
	 * @return the displayed text
	 */
	public String getDisplay() {
		return content;
	}

	/**
	 * The cached absolute x value. The {@link PIComponent#compile()} method
	 * updates this value.
	 * 
	 * @return the cached absolute x coordinate
	 */
	public final int getAbsoluteX() {
		return absX;
	}

	/**
	 * The cached absolute y value. The {@link PIComponent#compile()} method
	 * updates this value.
	 * 
	 * @return the cached absolute y coordinate
	 */
	public final int getAbsoluteY() {
		return absY;
	}

	/**
	 * Gets the absolute bounds of this component, and applies it to the
	 * preallocated rectangle. The {@link PIComponent#compile()} method updates
	 * the position of this rectangle.
	 * 
	 * @see PIComponent#getAbsoluteX()
	 * @see PIComponent#getAbsoluteY()
	 * @param r the rectangle to apply the bounds to
	 * @return the rectangle
	 */
	public final Rectangle getAbsoluteBounds(final Rectangle r) {
		r.setBounds(getAbsoluteX(), getAbsoluteY(), width,
				height);
		return r;
	}

	/**
	 * Gets the absolute bounds of this component. The
	 * {@link PIComponent#compile()} method updates the position of this
	 * rectangle.
	 * 
	 * @see PIComponent#getAbsoluteX()
	 * @see PIComponent#getAbsoluteY()
	 * @return the absolute bounds
	 */
	public final Rectangle getAbsoluteBounds() {
		return getAbsoluteBounds(new Rectangle());
	}

	/**
	 * Gets the relative bounds of this component and applies it to the
	 * preallocated rectangle.
	 * 
	 * @param r the rectangle to apply the bounds to
	 * @return the rectangle
	 */
	public final Rectangle getBounds(final Rectangle r) {
		r.setBounds(x, y, width, height);
		return r;
	}

	/**
	 * Gets the relative bounds of this component.
	 * 
	 * @return the relative bounds
	 */
	public final Rectangle getBounds() {
		return getBounds(new Rectangle());
	}

	/**
	 * Gets the x position of this component.
	 * 
	 * @return the x coordinate
	 */
	public final int getX() {
		return x;
	}

	/**
	 * Gets the y position of this component.
	 * 
	 * @return the y coordinate
	 */
	public final int getY() {
		return y;
	}

	/**
	 * Gets the width of this component.
	 * 
	 * @return the component's width
	 */
	public final int getWidth() {
		return width;
	}

	/**
	 * Gets the height of this component.
	 * 
	 * @return the component's height
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * Sets the textual content of this component.
	 * 
	 * @param s the new content
	 */
	public final void setContent(final String s) {
		this.content = s;
	}

	/**
	 * Sets the size of this component.
	 * 
	 * @param sWidth the new width
	 * @param sHeight the new height
	 */
	public void setSize(final int sWidth, final int sHeight) {
		this.width = sWidth;
		this.height = sHeight;
	}

	/**
	 * Sets the size of this component.
	 * 
	 * @param d the new size
	 */
	public final void setSize(final Dimension d) {
		setSize(d.width, d.height);
	}

	/**
	 * Sets the width of this component.
	 * 
	 * @param sWidth the new width
	 */
	public final void setWidth(final int sWidth) {
		setSize(width, getHeight());
	}

	/**
	 * Sets the height of this component.
	 * 
	 * @param sHeight the new height
	 */
	public final void setHeight(final int sHeight) {
		setSize(getWidth(), sHeight);
	}

	/**
	 * Sets the x coordinate of this component.The {@link PIComponent#compile()}
	 * method must be invoked before the absolute location will be updated.
	 * 
	 * @param sX the new x coordinate
	 */
	public final void setX(final int sX) {
		this.x = sX;
	}

	/**
	 * Sets the y coordinate of this component.The {@link PIComponent#compile()}
	 * method must be invoked before the absolute location will be updated.
	 * 
	 * @param sY the new y coordinate
	 */
	public final void setY(final int sY) {
		this.y = sY;
	}

	/**
	 * Sets the location of this component.The {@link PIComponent#compile()}
	 * method must be invoked before the absolute location will be updated.
	 * 
	 * @param sX the new x coordinate
	 * @param sY the new y coordinate
	 */
	public final void setLocation(final int sX, final int sY) {
		this.x = sX;
		this.y = sY;
	}

	/**
	 * Sets the location of this component. The {@link PIComponent#compile()}
	 * method must be invoked before the absolute location will be updated.
	 * 
	 * @param p the new location
	 */
	public final void setLocation(final Point2D p) {
		this.x = (int) p.getX();
		this.y = (int) p.getY();
	}

	/**
	 * Sets the focused state of this component.
	 * 
	 * @param value the new state
	 */
	public final void setFocused(final boolean value) {
		this.isFocused = value;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		Rectangle r = getAbsoluteBounds();
		if (r.contains(e.getPoint()) && isVisible()
				&& !e.isConsumed()) {
			isFocused = true;
			MouseEvent nE =
					new MouseEvent(e.getComponent(), e.getID(),
							e.getWhen(), e.getModifiers(),
							e.getX() - r.x, e.getY() - r.y,
							e.getClickCount(),
							e.isPopupTrigger(), e.getButton());
			nE.setSource(this);
			for (MouseListener l : mouseListeners) {
				l.mouseClicked(nE);
			}
		} else {
			isFocused = false;
		}
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		Rectangle r = getAbsoluteBounds();
		if (r.contains(e.getPoint()) && isVisible()
				&& !e.isConsumed()) {
			MouseEvent nE =
					new MouseEvent(e.getComponent(), e.getID(),
							e.getWhen(), e.getModifiers(),
							e.getX() - r.x, e.getY() - r.y,
							e.getClickCount(),
							e.isPopupTrigger(), e.getButton());
			nE.setSource(this);
			for (MouseListener l : mouseListeners) {
				l.mousePressed(nE);
			}
			isFocused = true;
		} else {
			isFocused = false;
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		Rectangle r = getAbsoluteBounds();
		if (r.contains(e.getPoint()) && isVisible()
				&& !e.isConsumed()) {
			MouseEvent nE =
					new MouseEvent(e.getComponent(), e.getID(),
							e.getWhen(), e.getModifiers(),
							e.getX() - r.x, e.getY() - r.y,
							e.getClickCount(),
							e.isPopupTrigger(), e.getButton());
			nE.setSource(this);
			for (MouseListener l : mouseListeners) {
				l.mouseReleased(nE);
			}
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		Rectangle r = getAbsoluteBounds();
		if (r.contains(e.getPoint()) && isVisible()
				&& !e.isConsumed()) {
			MouseEvent nE =
					new MouseEvent(e.getComponent(), e.getID(),
							e.getWhen(), e.getModifiers(),
							e.getX() - r.x, e.getY() - r.y,
							e.getClickCount(),
							e.isPopupTrigger(), e.getButton());
			nE.setSource(this);
			for (MouseMotionListener l : mouseMotionListeners) {
				l.mouseMoved(nE);
			}
			hovering = true;
		} else {
			hovering = false;
		}
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		Rectangle r = getAbsoluteBounds();
		if (r.contains(e.getPoint()) && isVisible()
				&& !e.isConsumed()) {
			MouseEvent nE =
					new MouseEvent(e.getComponent(), e.getID(),
							e.getWhen(), e.getModifiers(),
							e.getX() - r.x, e.getY() - r.y,
							e.getClickCount(),
							e.isPopupTrigger(), e.getButton());
			nE.setSource(this);
			for (MouseMotionListener l : mouseMotionListeners) {
				l.mouseDragged(nE);
			}
		}
	}

	/**
	 * Adds the mouse motion listener to the mouse motion listener array.
	 * 
	 * @param l the mouse motion listener to add
	 */
	public final void addMouseMotionListener(
			final MouseMotionListener l) {
		mouseMotionListeners.add(l);
	}

	/**
	 * Adds the mouse listener to the mouse listener array.
	 * 
	 * @param l the mouse listener to add
	 */
	public final void addMouseListener(final MouseListener l) {
		mouseListeners.add(l);
	}

	/**
	 * Adds the mouse wheel listener to the mouse wheel listener array.
	 * 
	 * @param l the mouse wheel listener to add
	 */
	public final void addMouseWheelListener(
			final MouseWheelListener l) {
		mouseWheelListeners.add(l);
	}

	/**
	 * Adds the key listener to the key listener array.
	 * 
	 * @param l the key listener to add
	 */
	public final void addKeyListener(final KeyListener l) {
		keyListeners.add(l);
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		if (isFocused && !e.isConsumed() && isVisible()) {
			for (KeyListener l : keyListeners) {
				l.keyPressed(e);
			}
		}
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		if (isFocused && !e.isConsumed() && isVisible()) {
			for (KeyListener l : keyListeners) {
				l.keyReleased(e);
			}
		}
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		if (isFocused && !e.isConsumed() && isVisible()) {
			for (KeyListener l : keyListeners) {
				l.keyTyped(e);
			}
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		if (hovering && !e.isConsumed() && isVisible()) {
			for (MouseWheelListener l : mouseWheelListeners) {
				l.mouseWheelMoved(e);
			}
		}
	}

	/**
	 * Sets if this component should clip it's contents.
	 * 
	 * @param val the new value
	 */
	public final void setClipContents(final boolean val) {
		this.clipContents = val;
	}

	/**
	 * Checks if this component clips contents.
	 * 
	 * @return if this component clips it's contents
	 */
	protected final boolean doesClipContents() {
		return this.clipContents;
	}

	/**
	 * Checks if this component is active.
	 * 
	 * @return the active state
	 */
	protected final boolean isActive() {
		return isActive;
	}

	/**
	 * Checks if this component currently has the mouse hovering over it.
	 * 
	 * @return the hovering
	 */
	protected final boolean isHovering() {
		return hovering;
	}
}
