package com.pi.server.logic.entity;

import com.pi.common.contants.Direction;
import com.pi.common.game.Entity;
import com.pi.server.Server;
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
					return;
				}
			}
		}
	}
}
