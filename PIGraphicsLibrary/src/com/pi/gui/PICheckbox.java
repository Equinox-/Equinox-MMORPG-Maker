package com.pi.gui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.pi.graphics.device.IGraphics;

public class PICheckbox extends PIComponent {

	public PICheckbox() {
		setStyleSet(GUIKit.checkboxSet, false);
	}

	public boolean isChecked() {
		return isActive;
	}

	public void setChecked(boolean checked) {
		this.isActive = checked;
	}

	@Override
	public PIStyle getCurrentStyle() {
		if (hovering && containsStyle(PIStyle.StyleType.Hover))
			return getStyle(PIStyle.StyleType.Hover);
		if (isActive && containsStyle(PIStyle.StyleType.Active))
			return getStyle(PIStyle.StyleType.Active);
		return getStyle(PIStyle.StyleType.Normal);
	}

	@Override
	public void paintBackground(IGraphics g) {
		PIStyle style;
		if (isVisible && (style = getCurrentStyle()) != null) {
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
	public void mouseClicked(MouseEvent e) {
		if (isFocused) {
			isActive = !isActive;
		}
		super.mouseClicked(e);
	}

	@Override
	public Rectangle getAbsolutePaddedBounds() {
		Rectangle bounds = getAbsoluteBounds();
		bounds.setBounds(bounds.x + bounds.height + 4, bounds.y, bounds.width,
				bounds.height);
		return bounds;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		super.keyPressed(e);
		if (e.getKeyChar() == '\n' && isFocused) {
			MouseEvent ev = new MouseEvent(e.getComponent(),
					MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,
					absX + (width / 2), absY + (height / 2), 1, false);
			ev.setSource(this);
			mouseClicked(ev);
		}
	}
}
