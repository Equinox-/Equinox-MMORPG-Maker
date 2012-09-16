package com.pi.gui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.pi.graphics.device.IGraphics;

/**
 * A component with a checkable box next to the component's text content.
 * 
 * @author Westin
 * 
 */
public class PICheckbox extends PIComponent {

	/**
	 * Create a checkbox with the default style set as defined in
	 * {@link GUIKit#CHECKBOX_STYLE_SET}.
	 */
	public PICheckbox() {
		setStyleSet(GUIKit.CHECKBOX_STYLE_SET, false);
	}

	/**
	 * If this checkbox is currently checked.
	 * 
	 * @return the checked state
	 */
	public final boolean isChecked() {
		return isActive();
	}

	/**
	 * Set the checked state of this checkbox.
	 * 
	 * @param checked
	 *            the new checked state
	 */
	public final void setChecked(final boolean checked) {
		this.setActive(checked);
	}

	@Override
	public final PIStyle getCurrentStyle() {
		if (isHovering() && containsStyle(PIStyle.StyleType.HOVER)) {
			return getStyle(PIStyle.StyleType.HOVER);
		}
		if (isActive() && containsStyle(PIStyle.StyleType.ACTIVE)) {
			return getStyle(PIStyle.StyleType.ACTIVE);
		}
		return getStyle(PIStyle.StyleType.NORMAL);
	}

	@Override
	public final void paintBackground(final IGraphics g) {
		if (isVisible()) {
			PIStyle style = getCurrentStyle();
			if (style == null) {
				return;
			}
			Rectangle bounds = getAbsoluteBounds();
			if (style.background != null) {
				g.setColor(style.background);
				g.fillRect(bounds.x, bounds.y, bounds.height, bounds.height);
			}
			if (style.border != null) {
				g.setColor(style.border);
				g.drawRect(bounds.x, bounds.y, bounds.height, bounds.height);
			}
		}
	}

	@Override
	public final void mouseClicked(final MouseEvent e) {
		if (isVisible() && !e.isConsumed() && isFocused()) {
			setActive(!isActive());
		}
		super.mouseClicked(e);
	}

	@Override
	public final Rectangle getAbsolutePaddedBounds() {
		Rectangle bounds = getAbsoluteBounds();
		bounds.setBounds(bounds.x + bounds.height + 4, bounds.y, bounds.width,
				bounds.height);
		return bounds;
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		super.keyPressed(e);
		if (e.getKeyChar() == '\n' && isFocused() && isVisible()
				&& !e.isConsumed()) {
			MouseEvent ev = new MouseEvent(e.getComponent(),
					MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,
					getAbsoluteX() + (getWidth() / 2), getAbsoluteY()
							+ (getHeight() / 2), 1, false);
			ev.setSource(this);
			mouseClicked(ev);
		}
	}
}
