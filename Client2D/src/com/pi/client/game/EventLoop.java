package com.pi.client.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.pi.client.Client;
import com.pi.client.ClientThread;
import com.pi.client.entity.ClientEntity;
import com.pi.common.constants.Direction;
import com.pi.common.constants.EntityConstants;
import com.pi.common.constants.InteractionButton;
import com.pi.common.constants.ItemConstants;
import com.pi.common.game.entity.Entity;
import com.pi.common.net.packet.Packet14ClientMove;
import com.pi.common.net.packet.Packet19Interact;

/**
 * The event processor loop.
 * 
 * @author Westin
 * 
 */
public class EventLoop extends ClientThread implements
		KeyListener {
	/**
	 * The maximum key code, for creating the array.
	 */
	private static final int MAXIMUM_KEY_CODE = 1024;
	/**
	 * The pressed state of various keys.
	 */
	private volatile boolean[] keyState =
			new boolean[MAXIMUM_KEY_CODE];

	/**
	 * The last time this client attacked something.
	 */
	private long lastAttackTime;

	/**
	 * The last time this client picked something up.
	 */
	private long lastPickupTime;

	/**
	 * The number of keys currently pressed. This is used to allow the thread to
	 * sleep when the user isn't using the keyboard.
	 */
	private int keyDownCount = 0;

	/**
	 * Creates an event loop for the given client.
	 * 
	 * @param sClient the client
	 */
	public EventLoop(final Client sClient) {
		super(sClient);
		createMutex();
		start();
	}

	@Override
	protected final void loop() {
		if (keyDownCount == 0) {
			synchronized (getMutex()) {
				try {
					getMutex().wait();
				} catch (InterruptedException e) {
				}
			}
		} else {
			ClientEntity cEnt =
					getClient().getEntityManager()
							.getLocalEntity();
			if (cEnt != null) {
				Direction dir = null;
				if (keyState[KeyEvent.VK_UP]) {
					dir = Direction.UP;
				} else if (keyState[KeyEvent.VK_DOWN]) {
					dir = Direction.DOWN;
				} else if (keyState[KeyEvent.VK_LEFT]) {
					dir = Direction.LEFT;
				} else if (keyState[KeyEvent.VK_RIGHT]) {
					dir = Direction.RIGHT;
				}
				long timeToAttack =
						EntityConstants.DEFAULT_ENTITY_ATTACK_SPEED;
				if (keyState[InteractionButton.ATTACK
						.getKeyCode()]
						&& System.currentTimeMillis() >= lastAttackTime
								+ timeToAttack) {
					getClient()
							.getNetwork()
							.send(Packet19Interact
									.create(InteractionButton.ATTACK));
					lastAttackTime = System.currentTimeMillis();
				}
				if (keyState[InteractionButton.GRAB.getKeyCode()]
						&& System.currentTimeMillis() >= lastPickupTime
								+ ItemConstants.DEFAULT_ITEM_PICKUP_SPEED) {
					getClient()
							.getNetwork()
							.send(Packet19Interact
									.create(InteractionButton.GRAB));
					lastPickupTime = System.currentTimeMillis();
				}
				if (dir != null && !cEnt.isMoving()) {
					Entity local = cEnt.getWrappedEntity();
					local.setDir(dir);
					if (local.canMove(getClient().getWorld())) {
						cEnt.doMovement(keyState[KeyEvent.VK_SHIFT]);
						getClient().getNetwork()
								.send(Packet14ClientMove
										.create(local));
					}
				}
			}
		}
	}

	@Override
	public void keyTyped(final KeyEvent e) {
	}

	@Override
	public final void keyPressed(final KeyEvent e) {
		if (e.getKeyCode() >= 0
				&& e.getKeyCode() < keyState.length) {
			if (!keyState[e.getKeyCode()]) {
				if (keyDownCount == 0) {
					synchronized (getMutex()) {
						getMutex().notify();
					}
				}
				keyDownCount++;
			}
			keyState[e.getKeyCode()] = true;
		}
	}

	@Override
	public final void keyReleased(final KeyEvent e) {
		if (e.getKeyCode() >= 0
				&& e.getKeyCode() < keyState.length) {
			if (keyState[e.getKeyCode()]) {
				keyDownCount--;
			}
			keyState[e.getKeyCode()] = false;
		}
	}

}
