package com.pi.common.contants;

import java.awt.event.KeyEvent;

public enum InteractionButton {
	ATTACK(KeyEvent.VK_ENTER), INTERACT(KeyEvent.VK_ENTER);
	private final int keyCode;

	private InteractionButton(int keyCode) {
		this.keyCode = keyCode;
	}

	public int getKeyCode() {
		return keyCode;
	}
}
