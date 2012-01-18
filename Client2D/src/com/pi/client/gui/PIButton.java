package com.pi.client.gui;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class PIButton extends PIComponent {
    @Override
    public void keyTyped(KeyEvent e) {
	super.keyPressed(e);
	if (e.getKeyChar() == '\n' && isFocused) {
	    System.out.println("PSEUDOCLICK");
	    mouseClicked(new MouseEvent(e.getComponent(),
		    MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0,
		    absX + (width / 2), absY + (height / 2), 1, false));
	}
    }
}
