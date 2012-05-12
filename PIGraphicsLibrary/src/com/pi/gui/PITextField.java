package com.pi.gui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class PITextField extends PIComponent {
    private Character mask = null;
    private Integer maxLength = null;

    public PITextField() {
	super();
	setStyleSet(GUIKit.textfieldSet, false);
    }

    @Override
    public String getDisplay() {
	if (mask != null) {
	    String disp = "";
	    for (int i = 0; i < content.length(); i++)
		disp += mask;
	    return disp + (isFocused ? "_" : "");
	}
	return content + (isFocused ? "_" : "");
    }

    @Override
    public void keyTyped(KeyEvent e) {
	super.keyTyped(e);
	if (isFocused) {
	    char c = e.getKeyChar();
	    if (c == '\b') {
		if (content.length() >= 1)
		    content = content.substring(0, content.length() - 1);
	    } else {
		if (maxLength == null || content.length() < maxLength)
		    content += c;
	    }
	}
    }

    public void setMaxLength(Integer max) {
	this.maxLength = max;
    }

    @Override
    public Rectangle getAbsolutePaddedBounds() {
	Rectangle r = getAbsoluteBounds();
	r.setBounds(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
	return r;
    }

    public void setMask(Character mask) {
	this.mask = mask;
    }
}
