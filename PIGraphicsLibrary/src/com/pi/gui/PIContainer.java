package com.pi.gui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import com.pi.graphics.device.IGraphics;

/**
 * An extension of the PIComponent class that allows for children components to
 * be added to this component.
 * 
 * @author Westin
 * 
 */
public class PIContainer extends PIComponent {
	/**
	 * The children this container has.
	 */
	private List<PIComponent> children =
			new ArrayList<PIComponent>();
	/**
	 * The tab index list, where each item on the list refers to the child with
	 * that index.
	 */
	private List<Integer> tabIndex = new ArrayList<Integer>();

	/**
	 * Adds the specified child to this container.
	 * 
	 * @param child the child to add
	 */
	public final void add(final PIComponent child) {
		children.add(child);
		child.setParent(this);
	}

	/**
	 * Gets all the children registered with this container.
	 * 
	 * @return the children of this container
	 */
	public final List<PIComponent> getChildren() {
		return children;
	}

	@Override
	public final void compile() {
		super.compile();
		for (PIComponent p : children) {
			p.compile();
		}
	}

	/**
	 * Sets the tab index of a specific component.
	 * 
	 * @param p the component to set the tab index for
	 * @param idx the tab index for this component
	 * @return if the component was registered in the tab index list
	 */
	public final boolean setTabIndex(final PIComponent p,
			final int idx) {
		int inIDX = children.indexOf(p);
		if (inIDX != -1) {
			int tCIDX = tabIndex.indexOf(inIDX);
			if (tCIDX != -1) {
				tabIndex.remove(tCIDX);
			}
			if (idx >= tabIndex.size()) {
				tabIndex.add(inIDX);
			} else if (idx >= 0) {
				tabIndex.add(idx, inIDX);
			}
			return true;
		}
		return false;
	}

	@Override
	public void render(final IGraphics g) {
		super.render(g);
		if (isVisible) {
			Rectangle currentBounds = null;
			Rectangle bounds = getAbsolutePaddedBounds();
			if (clipContents) {
				currentBounds = (Rectangle) g.getClip().clone();
			}
			for (PIComponent child : children) {
				if (clipContents) {
					g.setClip(bounds);
				}
				child.render(g);
			}
			if (clipContents) {
				g.setClip(currentBounds);
			}
		}
	}

	@Override
	public final void mouseClicked(final MouseEvent e) {
		super.mouseClicked(e);
		for (PIComponent child : children) {
			child.mouseClicked(e);
		}
	}

	@Override
	public final void mousePressed(final MouseEvent e) {
		super.mousePressed(e);
		for (PIComponent child : children) {
			child.mousePressed(e);
		}
	}

	@Override
	public final void mouseReleased(final MouseEvent e) {
		super.mouseReleased(e);
		for (PIComponent child : children) {
			child.mouseReleased(e);
		}
	}

	@Override
	public final void mouseMoved(final MouseEvent e) {
		super.mouseMoved(e);
		for (PIComponent child : children) {
			child.mouseMoved(e);
		}
	}

	@Override
	public final void mouseDragged(final MouseEvent e) {
		super.mouseDragged(e);
		for (PIComponent child : children) {
			child.mouseDragged(e);
		}
	}

	@Override
	public final void mouseWheelMoved(final MouseWheelEvent e) {
		super.mouseWheelMoved(e);
		for (PIComponent child : children) {
			child.mouseWheelMoved(e);
		}
	}

	@Override
	public final void keyReleased(final KeyEvent e) {
		super.keyReleased(e);
		for (PIComponent child : children) {
			child.keyReleased(e);
		}
	}

	@Override
	public final void keyPressed(final KeyEvent e) {
		super.keyPressed(e);
		for (PIComponent child : children) {
			child.keyPressed(e);
		}
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		boolean consumed = false;

		if (e.getKeyChar() == '\t') {
			int newIDX = getCurrentTabIndex() + 1;
			if (newIDX >= tabIndex.size()) {
				newIDX = 0;
			}
			if (newIDX < tabIndex.size()) {
				for (PIComponent c : children) {
					c.setFocused(false);
				}
				PIComponent c =
						children.get(tabIndex.get(newIDX));
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

	/**
	 * Gets the current tab index of this container. This is the last focused
	 * component.
	 * 
	 * @return the current tab index, or -1 if unknown
	 */
	public final int getCurrentTabIndex() {
		int idx = -1;
		for (int i = 0; i < children.size(); i++) {
			PIComponent c = children.get(i);
			if (c.isFocused) {
				int nI = tabIndex.indexOf(i);
				if (nI != -1) {
					idx = nI;
				}
			}
		}
		return idx;
	}
}
