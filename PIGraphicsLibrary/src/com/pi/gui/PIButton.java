package com.pi.gui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PIButton extends PIComponent {

	public PIButton() {
		super();
		setStyleSet(GUIKit.buttonSet, false);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		super.keyPressed(e);
		if (e.getKeyChar() == '\n' && isFocused) {
			MouseEvent ev = new MouseEvent(e.getComponent(),
					MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,
					absX + (width / 2), absY + (height / 2), 1, false);
			mouseClicked(ev);
		}
	}
}
