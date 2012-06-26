package com.pi.gui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * A component that is similar to a normal component, but with the button style
 * set and mouse click event activation when enter is pressed when focused.
 * 
 * @author Westin
 * 
 */
public class PIButton extends PIComponent {

	/**
	 * Create a normal button with the button style set as defined in
	 * {@link GUIKit#BUTTON_STYLE_SET}.
	 */
	public PIButton() {
		super();
		setStyleSet(GUIKit.BUTTON_STYLE_SET, false);
	}

	@Override
	public final void keyTyped(final KeyEvent e) {
		super.keyPressed(e);
		if (e.getKeyChar() == '\n' && isFocused) {
			MouseEvent ev =
					new MouseEvent(e.getComponent(),
							MouseEvent.MOUSE_CLICKED,
							System.currentTimeMillis(), 0, absX
									+ (width / 2), absY
									+ (height / 2), 1, false);
			mouseClicked(ev);
		}
	}
}
