package com.pi.common.contants;

import java.awt.event.KeyEvent;

public enum InteractionButton {
	ATTACK(KeyEvent.VK_ENTER, true), GRAB(KeyEvent.VK_ENTER, false);
	private final int keyCode;
	private final boolean inMyDirection;

	private InteractionButton(int keyCode, boolean inMyDirection) {
		this.keyCode = keyCode;
		this.inMyDirection = inMyDirection;
	}

	public int getKeyCode() {
		return keyCode;
	}

	public boolean isTargetInMyDirection() {
		return inMyDirection;
	}
}
