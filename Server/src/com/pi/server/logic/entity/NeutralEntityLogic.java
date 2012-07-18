package com.pi.server.logic.entity;

import com.pi.common.contants.Direction;
import com.pi.common.game.Entity;
import com.pi.common.game.LivingEntity;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet18Health;
import com.pi.server.Server;
import com.pi.server.client.Client;
import com.pi.server.entity.ServerEntity;

/**
 * Entity logic class that targets another getEntity(), or randomly moves if no
 * target is found.
 * 
 * @author Westin
 * 
 */
public class NeutralEntityLogic extends RandomEntityLogic {
	/**
	 * Creates an neutral logic instance for the given getEntity() and server.
	 * 
	 * @param entity the entity to create logic for
	 * @param server the server to bind to
	 */
	public NeutralEntityLogic(final ServerEntity entity,
			final Server server) {
		super(entity, server);
	}

	@Override
	public final void doLogic() {
		if (getServerEntity().isStillMoving()) {
			return;
		}
		int target = getServerEntity().getAttacker();
		if (target < 0) {
			super.doLogic();
		} else {
			ServerEntity wrapper =
					getServer().getEntityManager().getEntity(
							target);
			attack(wrapper);
		}
	}

	/**
	 * Moves towards and attacks the given entity.
	 * 
	 * @param wrapper the entity to attack
	 */
	protected final void attack(final ServerEntity wrapper) {
		if (wrapper != null) {
			Entity eTarget = wrapper.getWrappedEntity();
			if (eTarget != null) {
				int xDir = (eTarget.x - getEntity().x);
				int zDir = (eTarget.z - getEntity().z);
				Direction[] pDirs;
				if (xDir != 0) {
					if (zDir != 0) {
						pDirs =
								new Direction[] {
										Direction
												.getBestDirection(
														0, zDir),
										Direction
												.getBestDirection(
														xDir, 0) };
					} else {
						pDirs =
								new Direction[] { Direction
										.getBestDirection(xDir,
												0) };
					}
				} else {
					if (zDir != 0) {
						pDirs =
								new Direction[] { Direction
										.getBestDirection(0,
												zDir) };
					} else {
						pDirs = Direction.values();
					}
				}
				if (Math.abs(xDir) + Math.abs(zDir) != 1
						&& pDirs.length == 1) {
					int[] triedDirs = new int[0];
					if (pDirs.length >= 2) {
						triedDirs =
								new int[] { getRandom().nextInt(
										pDirs.length) };
						if (tryMove(pDirs[triedDirs[0]])) {
							return;
						}
					}
					for (int i = 0; i < pDirs.length; i++) {
						for (int tried : triedDirs) {
							if (tried == i) {
								continue;
							}
						}
						if (tryMove(pDirs[i])) {
							return;
						}
					}
				} else {
					// We are close enough to attack, but are we facing the
					// right way
					if (getEntity().getDir() != pDirs[0]) {
						getEntity().setDir(pDirs[0]);
						getServer().getEntityManager()
								.sendEntityRotate(
										getEntity()
												.getEntityID(),
										pDirs[0]);
					} else {
						// Attack!
						if (eTarget instanceof LivingEntity) {
							LivingEntity lE =
									(LivingEntity) eTarget;
							lE.setHealth(lE.getHealth() - 1);
							wrapper.setAttacker(getEntity()
									.getEntityID());
							// TODO Based on levels and stuff
							Client attackedClient =
									getServer()
											.getClientManager()
											.getClientByEntity(
													lE.getEntityID());
							if (lE.getHealth() <= 0) {
								getServer()
										.getEntityManager()
										.sendEntityDispose(
												eTarget.getEntityID());
								if (attackedClient != null) {
									attackedClient
											.onEntityDeath();
								}
							} else {
								if (attackedClient != null) {
									Packet pack =
											Packet18Health
													.create(lE);
									attackedClient
											.getNetClient()
											.send(pack);
								}
							}
						}
					}
				}
				return;
			}
		}
	}
}
