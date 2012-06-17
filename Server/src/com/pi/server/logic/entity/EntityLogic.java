package com.pi.server.logic.entity;

import com.pi.common.contants.Direction;
import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

public abstract class EntityLogic {
	protected final ServerEntity entity;
	protected final Server server;

	public EntityLogic(final ServerEntity entity, final Server server) {
		this.server = server;
		this.entity = entity;
	}

	protected boolean tryMove(Direction d) {
		if (entity.canMoveIn(server.getWorld().getSectorManager(), d)) {
			entity.setDir(d);
			entity.doTimedMovement(false);
			return true;
		}
		return false;
	}

	public abstract void doLogic();
}
