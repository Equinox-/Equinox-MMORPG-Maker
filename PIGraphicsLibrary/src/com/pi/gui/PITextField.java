package com.pi.gui;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	 * Action listeners that check for text changes.
	 */
	private List<ActionListener> actListener =
			new ArrayList<ActionListener>();

	/**
	 * The text to display when there is no content.
	 */
	private String exampleString = "";

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
		if (!isFocused() && getContent().length() == 0) {
			return exampleString;
		}
		String disp;
		if (mask != null) {
			char[] maskedCharacters = new char[getContent().length()];
			Arrays.fill(maskedCharacters, mask);
			disp = new String(maskedCharacters);
		} else {
			disp = getContent();
		}
		if (isFocused()) {
			return disp + "_";
		} else {
			return disp;
		}
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		super.keyTyped(e);
		if (isFocused()) {
			ActionEvent evt = null;
			char c = e.getKeyChar();
			if (c == '\b') {
				if (getContent().length() >= 1) {
					setContent(getContent().substring(0,
							getContent().length() - 1));
					evt = new ActionEvent(this, 0, getContent());
				}
			} else {
				if ((maxLength == -1 || getContent().length() < maxLength)
						&& c >= ' ' && c <= '~') {
					setContent(getContent() + c);
					evt = new ActionEvent(this, 0, getContent());
				}
			}
			if (evt != null) {
				for (ActionListener l : actListener) {
					l.actionPerformed(evt);
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

	/**
	 * Adds the action listener to the action listener array.
	 * 
	 * @param l the action listener to add
	 */
	public final void addActionListener(final ActionListener l) {
		actListener.add(l);
	}

	/**
	 * The text to display when there is no content in this text field.
	 * 
	 * @param s the example string
	 */
	public final void setExampleString(final String s) {
		this.exampleString = s;
	}
}
