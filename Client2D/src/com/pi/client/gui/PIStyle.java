package com.pi.client.gui;

import java.awt.Color;
import java.awt.Font;

import com.pi.common.contants.GraphicsConstants;
import com.pi.common.database.GraphicsObject;

public class PIStyle {
    public static enum StyleType {
	Normal, Active, Hover
    }

    public static class PIStyleSet {
	public PIStyle normal;
	public PIStyle active;
	public PIStyle hover;

	@Override
	public PIStyleSet clone() {
	    PIStyleSet clone = new PIStyleSet();
	    if (normal != null)
		clone.normal = normal.clone();
	    if (hover != null)
		clone.hover = hover.clone();
	    if (active != null)
		clone.active = active.clone();
	    return clone;
	}
    }

    public Color border = null;
    public Color background = null;
    public Color foreground = GraphicsConstants.fontColor;
    public Font font = GraphicsConstants.font;
    public GraphicsObject bgImage;
    public boolean stretchBackgroundX = false;
    public boolean stretchBackgroundY = false;
    public boolean vAlign = true;
    public boolean hAlign = true;

    @Override
    public PIStyle clone() {
	PIStyle clone = new PIStyle();
	clone.border = border;
	clone.background = background;
	clone.font = font;
	clone.foreground = foreground;
	clone.bgImage = bgImage;
	clone.stretchBackgroundX = stretchBackgroundX;
	clone.stretchBackgroundY = stretchBackgroundY;
	clone.vAlign = vAlign;
	clone.hAlign = hAlign;
	return clone;
    }

    public void setFontSize(float size) {
	font = font.deriveFont(size);
    }
}
