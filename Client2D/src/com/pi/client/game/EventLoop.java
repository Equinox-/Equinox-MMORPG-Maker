package com.pi.client.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.pi.client.Client;
import com.pi.client.ClientThread;
import com.pi.client.entity.ClientEntity;
import com.pi.common.contants.Direction;
import com.pi.common.contants.EntityConstants;
import com.pi.common.database.def.EntityDef;
import com.pi.common.game.Entity;
import com.pi.common.net.packet.Packet14ClientMove;
import com.pi.common.net.packet.Packet19Attack;

/**
 * The event processor loop.
 * 
 * @author Westin
 * 
 */
public class EventLoop extends ClientThread implements
		KeyListener {
	/**
	 * The pressed state of various keys.
	 */
	private boolean[] keyState = new boolean[512];

	/**
	 * The last time this client attacked something.
	 */
	private long lastAttackTime;

	/**
	 * Creates an event loop for the given client.
	 * 
	 * @param sClient the client
	 */
	public EventLoop(final Client sClient) {
		super(sClient);
		start();
	}

	@Override
	protected final void loop() {
		ClientEntity cEnt =
				getClient().getEntityManager().getLocalEntity();
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
			EntityDef eDef =
					getClient()
							.getDefs()
							.getEntityLoader()
							.getDef(cEnt.getWrappedEntity()
									.getEntityDef());
			if (eDef != null) {
				timeToAttack = eDef.getAttackSpeed();
			}
			if (System.currentTimeMillis() >= lastAttackTime
					+ timeToAttack) {
				getClient().getNetwork().send(
						new Packet19Attack());
				lastAttackTime = System.currentTimeMillis();
			}
			if (dir != null && !cEnt.isMoving()) {
				Entity local = cEnt.getWrappedEntity();
				local.setDir(dir);
				if (local.canMove(getClient().getWorld())) {
					cEnt.doMovement(keyState[KeyEvent.VK_SHIFT]);
					getClient().getNetwork().send(
							Packet14ClientMove.create(local));
				}
			}
		}
		Thread.yield();
	}

	@Override
	public void keyTyped(final KeyEvent e) {
	}

	@Override
	public final void keyPressed(final KeyEvent e) {
		if (e.getKeyCode() >= 0
				&& e.getKeyCode() < keyState.length) {
			keyState[e.getKeyCode()] = true;
		}
	}

	@Override
	public final void keyReleased(final KeyEvent e) {
		if (e.getKeyCode() >= 0
				&& e.getKeyCode() < keyState.length) {
			keyState[e.getKeyCode()] = false;
		}
	}

}
