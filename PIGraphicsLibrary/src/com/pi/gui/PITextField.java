package com.pi.gui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * An extension of the component class that provides a text field.
 * 
 * @author Westin
 * 
 */
public class PITextField extends PIComponent {
	/**
	 * The mask applied to every character of this text, or <code>null</code> if
	 * there is no mask.
	 */
	private Character mask = null;
	/**
	 * The maximum length of this text field, or <code>-1</code> if there is no
	 * limit.
	 */
	private int maxLength = -1;

	/**
	 * Creates a new text field with the default text field style set.
	 * 
	 * @see GUIKit#TEXTFIELD_STYLE_SET
	 */
	public PITextField() {
		super();
		setStyleSet(GUIKit.TEXTFIELD_STYLE_SET, false);
	}

	@Override
	public final String getDisplay() {
		String disp;
		if (mask != null) {
			char[] maskedCharacters = new char[content.length()];
			Arrays.fill(maskedCharacters, mask);
			disp = new String(maskedCharacters);
		} else {
			disp = content;
		}
		if (isFocused) {
			return disp + "_";
		} else {
			return disp;
		}
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		super.keyTyped(e);
		if (isFocused) {
			char c = e.getKeyChar();
			if (c == '\b') {
				if (content.length() >= 1) {
					content =
							content.substring(0,
									content.length() - 1);
				}
			} else {
				if (maxLength == -1
						|| content.length() < maxLength) {
					content += c;
				}
			}
		}
	}

	/**
	 * Sets the maximum length of this text field, or <code>-1</code> if there
	 * is no limit.
	 * 
	 * @param max the maximum length
	 */
	public final void setMaxLength(final Integer max) {
		this.maxLength = max;
	}

	@Override
	public final Rectangle getAbsolutePaddedBounds() {
		Rectangle r = getAbsoluteBounds();
		r.setBounds(r.x + 2, r.y + 2, r.width - 4, r.height - 4);
		return r;
	}

	/**
	 * Sets the character mask of this text field, or <code>null</code> if there
	 * isn't one.
	 * 
	 * @param sMask the character mask
	 */
	public final void setMask(final Character sMask) {
		this.mask = sMask;
	}
}
