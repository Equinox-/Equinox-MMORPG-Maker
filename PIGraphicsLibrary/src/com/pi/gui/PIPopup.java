package com.pi.gui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.pi.graphics.device.IGraphics;

/**
 * A stand alone popup component.
 * 
 * @author Westin
 * 
 */
public class PIPopup implements MouseListener {
	/**
	 * The text content of this popup.
	 */
	private String content;
	/**
	 * If this popup should show a loading bar.
	 * 
	 * @see GUIKit.Graphics#LOADER
	 */
	private boolean loadingBar;
	/**
	 * If this popup should show a close button.
	 */
	private boolean closeable;
	/**
	 * If this popup is visible.
	 */
	private boolean isVisible = false;
	/**
	 * The cached position of the close button.
	 */
	private Rectangle closeButton = null;

	/**
	 * Sets the text content of this popup.
	 * 
	 * @param s the text content
	 */
	public final void setContent(final String s) {
		this.content = s;
	}

	/**
	 * Checks if this popup is visible.
	 * 
	 * @return if this popup is visible
	 */
	public final boolean isVisible() {
		return isVisible;
	}

	/**
	 * Sets if this popup should display a loading bar.
	 * 
	 * @see GUIKit.Graphics#LOADER
	 * @param value the new value
	 */
	public final void displayLoadingBar(final boolean value) {
		this.loadingBar = value;
	}

	/**
	 * Sets if this popup is visible.
	 * 
	 * @param value the new value
	 */
	public final void setVisible(final boolean value) {
		this.isVisible = value;
	}

	/**
	 * Sets if this popup should have a close button.
	 * 
	 * @param value the new value
	 */
	public final void setCloseable(final boolean value) {
		this.closeable = value;
	}

	/**
	 * Renders this popup that is centered according to the provided width and
	 * height.
	 * 
	 * @param g the graphics object
	 * @param xOffset the x offset of the container
	 * @param yOffset the y offset of the container
	 * @param width the width of the container
	 * @param height the height of the container
	 */
	public final void render(final IGraphics g,
			final int xOffset, final int yOffset,
			final int width, final int height) {
		if (isVisible) {
			Rectangle container =
					new Rectangle(width / 4 + xOffset, height
							/ 4 + yOffset, width / 2, height / 2);
			if (content == null || content.length() <= 0) {
				container.setBounds(width / 4 + xOffset,
						(height / 2) + yOffset
								- (height / 15 + 4), width / 2,
						height / 15 + 4);
			}
			if (GUIKit.DEFAULT_CONTAINER_STYLE.background != null) {
				g.setColor(GUIKit.DEFAULT_CONTAINER_STYLE.background);
				g.fillRect(container);
			}
			if (GUIKit.DEFAULT_CONTAINER_STYLE.border != null) {
				g.setColor(GUIKit.DEFAULT_CONTAINER_STYLE.border);
				g.drawRect(container);
			}
			if (loadingBar) {
				g.drawImage(GUIKit.Graphics.LOADER,
						container.x + 2, container.y
								+ container.height - 2
								- (height / 15),
						container.width - 4, height / 15);
			}
			if (content != null) {
				g.drawWrappedText(
						new Rectangle(
								container.x + 2,
								container.y + 2
										+ (closeable ? 20 : 0),
								container.width - 4,
								container.height
										- (loadingBar ? (height / 15)
												: 0) - 4
										- (closeable ? 20 : 0)),
						GUIKit.DEFAULT_CONTAINER_STYLE.font,
						content,
						GUIKit.DEFAULT_CONTAINER_STYLE.foreground,
						GUIKit.DEFAULT_CONTAINER_STYLE.hAlign,
						GUIKit.DEFAULT_CONTAINER_STYLE.vAlign);
			}
			if (closeable) {
				g.drawText(
						"X",
						container.x + container.width - 18,
						container.y + 2,
						GUIKit.DEFAULT_CONTAINER_STYLE.font,
						GUIKit.DEFAULT_CONTAINER_STYLE.foreground);
				closeButton =
						new Rectangle(container.x
								+ container.width - 18,
								container.y + 2, 16, 16);
			}
		}
	}

	@Override
	public final void mouseClicked(final MouseEvent e) {
		if (isVisible && closeable && closeButton != null) {
			if (closeButton.contains(e.getPoint())) {
				isVisible = false;
			}
		}
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
	}

	@Override
	public final void mousePressed(final MouseEvent e) {
	}

	@Override
	public final void mouseReleased(final MouseEvent e) {
	}
}
