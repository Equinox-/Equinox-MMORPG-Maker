package com.pi.common.database.def.entity;

import com.pi.common.util.ClassIntPairManager;

/**
 * Class managing entity definition component registration and identification.
 * 
 * @author Westin
 * 
 */
public final class EntityDefComponentManager {
	/**
	 * The pair manager that is currently in use.
	 */
	private static final ClassIntPairManager<EntityDefComponent> INSTANCE =
			new ClassIntPairManager<EntityDefComponent>();

	/**
	 * Gets the current pair manager INSTANCE being used by this entity
	 * definition component manager.
	 * 
	 * @return the pair manager
	 */
	public static ClassIntPairManager<EntityDefComponent> getInstance() {
		return INSTANCE;
	}

	static {
		INSTANCE.registerPair(HealthDefComponent.class);
		INSTANCE.registerPair(LogicDefComponent.class);
		INSTANCE.trimMaps();
	}

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private EntityDefComponentManager() {
	}
}
