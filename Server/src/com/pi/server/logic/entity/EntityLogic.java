package com.pi.server.logic.entity;

import com.pi.common.contants.Direction;
import com.pi.common.database.Location;
import com.pi.common.game.Entity;
import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

public abstract class EntityLogic {
	protected final ServerEntity sEntity;
	protected final Entity entity;
	protected final Server server;

	public EntityLogic(final ServerEntity entity, final Server server) {
		this.server = server;
		this.sEntity = entity;
		this.entity = sEntity.getWrappedEntity();
	}

	protected boolean tryMove(Direction d) {
		if (entity.canMoveIn(server.getWorld().getSectorManager(), d)) {
			entity.setDir(d);
			Location curr = new Location(entity.x, entity.plane, entity.z);
			sEntity.doTimedMovement(false);
			server.getServerEntityManager().sendEntityMove(
					entity.getEntityID(), curr, entity, d);
			return true;
		}
		return false;
	}

	public abstract void doLogic();
}
