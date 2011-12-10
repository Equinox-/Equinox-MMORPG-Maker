package com.pi.client.gui;

import java.awt.Rectangle;

import com.pi.client.graphics.device.IGraphics;

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
    public Rectangle getAbsolutePaddedBounds() {
	Rectangle bounds = getAbsoluteBounds();
	bounds.setBounds(bounds.x + bounds.height + 4, bounds.y, bounds.width,
		bounds.height);
	return bounds;
    }
}
