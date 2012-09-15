package com.pi.server.logic.entity;

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
					getServer().getEntityManager().getEntityContainer(
							target);
			if (wrapper != null) {
				attack(wrapper);
			}
		}
	}
}
