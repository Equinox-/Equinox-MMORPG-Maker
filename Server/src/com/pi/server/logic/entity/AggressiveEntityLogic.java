package com.pi.server.logic.entity;

import java.util.Iterator;

import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

/**
 * Entity logic class that targets another getEntity(), or randomly moves if no
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
	 * Creates an aggressive logic instance for the given getEntity() and
	 * server.
	 * 
	 * @param entity the entity to create logic for
	 * @param server the server to bind to
	 */
	public AggressiveEntityLogic(final ServerEntity entity,
			final Server server) {
		super(entity, server);
	}

	/**
	 * Find a new target for this getEntity().
	 * 
	 * @return the new target, or <code>null</code> if not found
	 */
	public final ServerEntity grabNewTarget() {
		Iterator<ServerEntity> entList =
				getServer().getEntityManager()
						.getEntitiesInSector(
								getEntity().getSectorLocation());
		int minDist = Integer.MAX_VALUE;
		ServerEntity best = null;
		while (entList.hasNext()) {
			ServerEntity e = entList.next();
			if (e != getServerEntity()) {
				int nDist =
						Math.abs(e.getWrappedEntity().x
								- getEntity().x)
								+ Math.abs(e.getWrappedEntity().z
										- getEntity().z);
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
		if (getServerEntity().isStillMoving()) {
			return;
		}

		if (target < 0) {
			if (getServerEntity().getAttacker() != -1) {
				target = getServerEntity().getAttacker();
			} else {
				ServerEntity nT = grabNewTarget();
				if (nT != null) {
					target = nT.getWrappedEntity().getEntityID();
				} else {
					super.doLogic();
				}
			}
		} else {
			ServerEntity wrapper =
					getServer().getEntityManager().getEntityContainer(
							this.target);
			if (wrapper != null) {
				attack(wrapper);
			} else {
				this.target = -1;
			}
		}
	}
}
