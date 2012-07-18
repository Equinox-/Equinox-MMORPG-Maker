package com.pi.server.logic.entity;

import java.util.Random;

import com.pi.common.contants.Direction;
import com.pi.common.database.Location;
import com.pi.common.game.Entity;
import com.pi.common.game.LivingEntity;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet18Health;
import com.pi.server.Server;
import com.pi.server.client.Client;
import com.pi.server.entity.ServerEntity;

/**
 * Super class for any entity logic.
 * 
 * @author Westin
 * 
 */
public abstract class EntityLogic {
	/**
	 * The entity wrapper this logic instance is bound to.
	 */
	private final ServerEntity sEntity;
	/**
	 * The server this logic instance is bound to.
	 */
	private final Server server;

	/**
	 * The random instance bound to this logic instance.
	 */
	private final Random rand = new Random();

	/**
	 * Creates a logic instance of the given server entity and server.
	 * 
	 * @param sSEntity the entity wrapper
	 * @param sServer the server
	 */
	public EntityLogic(final ServerEntity sSEntity,
			final Server sServer) {
		this.server = sServer;
		this.sEntity = sSEntity;
	}

	/**
	 * Gets the server this logic instance is bound to.
	 * 
	 * @return the server instance
	 */
	protected final Server getServer() {
		return server;
	}

	/**
	 * Gets the entity this logic instance is bound to.
	 * 
	 * @return the entity
	 */
	protected final Entity getEntity() {
		return sEntity.getWrappedEntity();
	}

	/**
	 * Gets the random instance this logic is bound to.
	 * 
	 * @return the random instance
	 */
	protected final Random getRandom() {
		return rand;
	}

	/**
	 * Gets the entity wrapper this logic instance is bound to.
	 * 
	 * @return the server entity
	 */
	protected final ServerEntity getServerEntity() {
		return sEntity;
	}

	/**
	 * Try to move the entity in the given direction, or a randomly choosen
	 * direction from the array.
	 * 
	 * @param d the direction(s) to move in
	 * @return if the entity was moved
	 */
	protected final boolean tryMove(final Direction... d) {
		if (sEntity.isStillMoving()) {
			return false;
		}

		int triedDir = -1;
		if (d.length >= 2) {
			triedDir = getRandom().nextInt(d.length);
			if (getEntity().canMoveIn(server.getWorld(),
					d[triedDir])) {
				getEntity().setDir(d[triedDir]);
				Location curr =
						new Location(getEntity().x,
								getEntity().plane, getEntity().z);
				sEntity.doTimedMovement();
				server.getEntityManager().sendEntityMove(
						getEntity().getEntityID(), curr,
						getEntity(), d[triedDir]);
				return true;
			}
		}
		for (int i = 0; i < d.length; i++) {
			if (triedDir == i) {
				continue;
			}
			if (getEntity().canMoveIn(server.getWorld(), d[i])) {
				getEntity().setDir(d[i]);
				Location curr =
						new Location(getEntity().x,
								getEntity().plane, getEntity().z);
				sEntity.doTimedMovement();
				server.getEntityManager().sendEntityMove(
						getEntity().getEntityID(), curr,
						getEntity(), d[i]);
				return true;
			}
		}
		return false;
	}

	/**
	 * Does the logic for this logic instance.
	 */
	public abstract void doLogic();

	/**
	 * Gets the possible directions that can be taken to get to the target, or
	 * all the directions specified by {@link Direction#values()} if on the
	 * target.
	 * 
	 * @param l the target
	 * @return the possible directions
	 */
	protected final Direction[] getDirectionsTo(final Location l) {
		int xDir = (l.x - getEntity().x);
		int zDir = (l.z - getEntity().z);
		Direction[] pDirs;
		if (xDir != 0) {
			if (zDir != 0) {
				pDirs =
						new Direction[] {
								Direction.getBestDirection(0,
										zDir),
								Direction.getBestDirection(xDir,
										0) };
			} else {
				pDirs =
						new Direction[] { Direction
								.getBestDirection(xDir, 0) };
			}
		} else {
			if (zDir != 0) {
				pDirs =
						new Direction[] { Direction
								.getBestDirection(0, zDir) };
			} else {
				pDirs = Direction.values();
			}
		}
		return pDirs;
	}

	/**
	 * Moves towards and attacks the given entity.
	 * 
	 * @param wrapper the entity to attack
	 */
	protected final void attack(final ServerEntity wrapper) {
		Entity eTarget = wrapper.getWrappedEntity();
		if (eTarget != null) {
			int xDir = (eTarget.x - getEntity().x);
			int zDir = (eTarget.z - getEntity().z);
			Direction[] pDirs = getDirectionsTo(eTarget);
			if (Math.abs(xDir) + Math.abs(zDir) != 1
					&& pDirs.length == 1) {
				tryMove(pDirs);
			} else {
				// We are close enough to attack, but are we facing the
				// right way
				if (getEntity().getDir() != pDirs[0]) {
					getEntity().setDir(pDirs[0]);
					getServer().getEntityManager()
							.sendEntityRotate(
									getEntity().getEntityID(),
									pDirs[0]);
				} else {
					// Attack!
					if (eTarget instanceof LivingEntity) {
						LivingEntity lE = (LivingEntity) eTarget;
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
								attackedClient.onEntityDeath();
							}
						} else {
							if (attackedClient != null) {
								Packet pack =
										Packet18Health
												.create(lE);
								attackedClient.getNetClient()
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
