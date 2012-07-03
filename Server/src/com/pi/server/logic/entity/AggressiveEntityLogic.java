package com.pi.server.logic.entity;

import java.util.List;

import com.pi.common.contants.Direction;
import com.pi.common.game.Entity;
import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

/**
 * Entity logic class that targets another entity, or randomly moves if no
 * target is found.
 * 
 * @author Westin
 * 
 */
public class AggressiveEntityLogic extends RandomEntityLogic {
	/**
	 * The target of this logic instance.
	 */
	private int target = -1;

	/**
	 * Creates an aggressive logic instance for the given entity and server.
	 * 
	 * @param entity the entity to create logic for
	 * @param server the server to bind to
	 */
	public AggressiveEntityLogic(final ServerEntity entity,
			final Server server) {
		super(entity, server);
	}

	/**
	 * Find a new target for this entity.
	 * 
	 * @return the new target, or <code>null</code> if not found
	 */
	public final ServerEntity grabNewTarget() {
		List<ServerEntity> entList =
				server.getServerEntityManager()
						.getEntitiesInSector(
								entity.getSectorLocation());
		int minDist = Integer.MAX_VALUE;
		ServerEntity best = null;
		for (ServerEntity e : entList) {
			if (e != sEntity) {
				int nDist =
						Math.abs(e.getWrappedEntity().x
								- entity.x)
								+ Math.abs(e.getWrappedEntity().z
										- entity.z);
				if (nDist < minDist) {
					minDist = nDist;
					best = e;
				}
			}
		}
		return best;
	}

	@Override
	public final void doLogic() {
		if (sEntity.isStillMoving()) {
			return;
		}

		if (target < 0) {
			ServerEntity nT = grabNewTarget();
			if (nT != null) {
				target = nT.getWrappedEntity().getEntityID();
			} else {
				super.doLogic();
			}
		} else {
			ServerEntity wrapper =
					server.getServerEntityManager().getEntity(
							this.target);
			if (wrapper != null) {
				Entity eTarget = wrapper.getWrappedEntity();
				if (eTarget != null) {
					int xDir = (eTarget.x - entity.x);
					int zDir = (eTarget.z - entity.z);
					Direction[] pDirs;
					if (xDir != 0) {
						if (zDir != 0) {
							pDirs =
									new Direction[] {
											Direction
													.getBestDirection(
															0,
															zDir),
											Direction
													.getBestDirection(
															xDir,
															0) };
						} else {
							pDirs =
									new Direction[] { Direction
											.getBestDirection(
													xDir, 0) };
						}
					} else {
						if (zDir != 0) {
							pDirs =
									new Direction[] { Direction
											.getBestDirection(0,
													zDir) };
						} else {
							pDirs = new Direction[0];
						}
					}
					int[] triedDirs = new int[0];
					if (pDirs.length >= 2) {
						triedDirs =
								new int[] { rand
										.nextInt(pDirs.length) };
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
					return;
				}
			}
		}
		this.target = -1;
	}
}
