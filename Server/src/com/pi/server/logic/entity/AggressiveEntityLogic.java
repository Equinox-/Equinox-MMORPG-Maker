package com.pi.server.logic.entity;

import java.util.List;

import com.pi.common.contants.Direction;
import com.pi.common.game.Entity;
import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

public class AggressiveEntityLogic extends RandomEntityLogic {
	private int target = -1;

	public AggressiveEntityLogic(ServerEntity entity, Server server) {
		super(entity, server);
	}

	public ServerEntity grabNewTarget() {
		List<ServerEntity> entList = server.getServerEntityManager()
				.getEntitiesInSector(entity.getSectorLocation());
		int minDist = Integer.MAX_VALUE;
		ServerEntity best = null;
		for (ServerEntity e : entList) {
			if (e != sEntity) {
				int nDist = Math.abs(e.getWrappedEntity().x - entity.x)
						+ Math.abs(e.getWrappedEntity().z - entity.z);
				if (nDist < minDist) {
					minDist = nDist;
					best = e;
				}
			}
		}
		return best;
	}

	@Override
	public void doLogic() {
		if (sEntity.isStillMoving())
			return;

		if (target < 0) {
			ServerEntity nT = grabNewTarget();
			if (nT != null) {
				target = nT.getWrappedEntity().getEntityID();
			} else {
				super.doLogic();
			}
		} else {
			ServerEntity wrapper = server.getServerEntityManager().getEntity(
					this.target);
			if (wrapper != null) {
				Entity target = wrapper.getWrappedEntity();
				if (target != null) {
					int xDir = (target.x - entity.x);
					int zDir = (target.z - entity.z);
					Direction[] pDirs;
					if (xDir != 0) {
						if (zDir != 0) {
							pDirs = new Direction[] {
									zDir < 0 ? Direction.UP : Direction.DOWN,
									xDir < 0 ? Direction.LEFT : Direction.RIGHT };
						} else {
							pDirs = new Direction[] { xDir < 0 ? Direction.LEFT
									: Direction.RIGHT };
						}
					} else {
						if (zDir != 0) {
							pDirs = new Direction[] { zDir < 0 ? Direction.UP
									: Direction.DOWN };
						} else {
							pDirs = new Direction[0];
						}
					}
					int[] triedDirs = new int[0];
					if (pDirs.length >= 2) {
						triedDirs = new int[] { rand.nextInt(pDirs.length) };
						if (tryMove(pDirs[triedDirs[0]]))
							return;
					}
					for (int i = 0; i < pDirs.length; i++) {
						for (int tried : triedDirs)
							if (tried == i)
								continue;
						if (tryMove(pDirs[i]))
							return;
					}
					return;
				}
			}
		}
		this.target = -1;
	}
}
