package com.pi.server.entity;

import com.pi.common.contants.MovementConstants;
import com.pi.common.game.Entity;
import com.pi.server.logic.entity.EntityLogic;

public class ServerEntity {
	private long nextMove = -1;
	private EntityLogic logicClass;
	private Entity wrap;

	public ServerEntity(Entity wrapped) {
		this.wrap = wrapped;
	}

	public boolean isStillMoving() {
		return nextMove != -1 && nextMove > System.currentTimeMillis();
	}

	public void doTimedMovement(boolean run) {
		nextMove = System.currentTimeMillis()
				+ (run ? MovementConstants.RUN_TIME
						: MovementConstants.WALK_TIME);
		wrap.doMovement();
	}

	public EntityLogic getLogic() {
		return logicClass;
	}

	public boolean assignLogic(EntityLogic l) {
		if (logicClass == null) {
			logicClass = l;
			return true;
		}
		return false;
	}

	public Entity getWrappedEntity() {
		return wrap;
	}
}
