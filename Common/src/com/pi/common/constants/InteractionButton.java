package com.pi.common.constants;

import java.awt.event.KeyEvent;

/**
 * An enum that represents different interaction buttons, to be handled by the
 * server.
 * 
 * @author westin
 * 
 */
public enum InteractionButton {
	/**
	 * The attack button.
	 */
	ATTACK(KeyEvent.VK_CONTROL, true),
	/**
	 * The pickup item button.
	 */
	GRAB(KeyEvent.VK_ENTER, false);
	/**
	 * The button's key code.
	 */
	private final int keyCode;
	/**
	 * If <code>true</code> the player should interact with whatever is in front
	 * of them, if <code>false</code> they should work with what is below them.
	 */
	private final boolean inMyDirection;

	/**
	 * Creates an enum element with the given key code and direction setting.
	 * 
	 * @param bKeyCode the key code
	 * @param isInMyDirection if the player should interact with what they are
	 *            facing, or what they are on.
	 */
	private InteractionButton(final int bKeyCode,
			final boolean isInMyDirection) {
		this.keyCode = bKeyCode;
		this.inMyDirection = isInMyDirection;
	}

	/**
	 * Gets the key code for this button.
	 * 
	 * @see java.awt.KeyEvent
	 * @return the key code ID number
	 */
	public int getKeyCode() {
		return keyCode;
	}

	/**
	 * Checks if the interaction target for this interaction type is in the
	 * direction the player is facing, or directly under the player.
	 * 
	 * @return the directional information
	 */
	public boolean isTargetInMyDirection() {
		return inMyDirection;
	}
}
