package com.pi.server.logic.entity;

import java.util.Random;

import com.pi.common.contants.Direction;
import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

public class RandomEntityLogic extends EntityLogic {
	protected final Random rand = new Random();
	private final int randomMovementMin = 3;
	private final int randomMovementMax = 5;
	private final float randomMovementChance = .25f;

	private int currentMoveCount = 0;

	public RandomEntityLogic(ServerEntity entity, Server server) {
		super(entity, server);
	}

	@Override
	public void doLogic() {
		if (sEntity.isStillMoving())
			return;

		if (currentMoveCount > 0) {
			// It would appear that we should move randomly.
			Direction d = null;
			for (int i = 0; i < 3
					&& (d == null || entity.getDir().getInverse() == d); i++)
				// The inverse thing is to try to not just have entities moving
				// back and forth
				d = Direction.values()[rand.nextInt(Direction.values().length)];

			if (tryMove(d)) {
				currentMoveCount--;
			}
		} else if (rand.nextFloat() < randomMovementChance) {
			currentMoveCount = rand.nextInt(randomMovementMax
					- randomMovementMin)
					+ randomMovementMin;
		}
	}
}
