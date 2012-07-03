package com.pi.server.logic.entity;

import java.util.Random;

import com.pi.common.contants.Direction;
import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

/**
 * Entity logic class that randomly moves.
 * 
 * @author Westin
 * 
 */
public class RandomEntityLogic extends EntityLogic {
	/**
	 * The random movement length minimum.
	 */
	private static final int RANDOM_MOVEMENT_MIN = 3;
	/**
	 * The random movement length maximum.
	 */
	private static final int RANDOM_MOVEMENT_MAX = 5;
	/**
	 * The random movement chance. Higher has a greater chance.
	 */
	private static final float RANDOM_MOVEMENT_CHANCE = .25f;

	/**
	 * The random instance bound to this logic instance.
	 */
	protected final Random rand = new Random();

	/**
	 * The current number of moves since the last movement start.
	 */
	private int currentMoveCount = 0;

	/**
	 * Create a random entity logic instance for the given entity wrapper and
	 * server.
	 * 
	 * @param sEntity the entity wrapper
	 * @param sServer the server
	 */
	public RandomEntityLogic(final ServerEntity sEntity,
			final Server sServer) {
		super(sEntity, sServer);
	}

	@Override
	public void doLogic() {
		if (sEntity.isStillMoving()) {
			return;
		}

		if (currentMoveCount > 0) {
			// It would appear that we should move randomly.
			Direction d = null;
			for (int i = 0; i < 3
					&& (d == null || entity.getDir()
							.getInverse() == d); i++) {
				// The inverse thing is to try to not just have entities moving
				// back and forth. Three tries.
				d =
						Direction.values()[rand
								.nextInt(Direction.values().length)];
			}
			if (tryMove(d)) {
				currentMoveCount--;
			}
		} else if (rand.nextFloat() < RANDOM_MOVEMENT_CHANCE) {
			currentMoveCount =
					rand.nextInt(RANDOM_MOVEMENT_MAX
							- RANDOM_MOVEMENT_MIN)
							+ RANDOM_MOVEMENT_MIN;
		}
	}
}
