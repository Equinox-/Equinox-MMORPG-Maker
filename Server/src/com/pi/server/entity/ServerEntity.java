package com.pi.server.entity;

import com.pi.common.contants.MovementConstants;
import com.pi.common.game.Entity;

public class ServerEntity extends Entity {
	private long nextMove = -1;

	public ServerEntity() {
		super();
	}

	public ServerEntity(int entityDef) {
		super(entityDef);
	}

	public boolean isStillMoving() {
		return nextMove != -1 && nextMove > System.currentTimeMillis();
	}

	public void doTimedMovement(boolean run) {
		nextMove = System.currentTimeMillis()
				+ (run ? MovementConstants.RUN_TIME
						: MovementConstants.WALK_TIME);
		super.doMovement();
	}
}
