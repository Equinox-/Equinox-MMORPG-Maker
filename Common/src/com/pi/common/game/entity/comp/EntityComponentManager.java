package com.pi.common.game.entity.comp;

import com.pi.common.util.ClassIntPairManager;

/**
 * Class managing entity component registration and identification.
 * 
 * @author Westin
 * 
 */
public final class EntityComponentManager {
	/**
	 * The pair manager that is currently in use.
	 */
	private static final ClassIntPairManager<EntityComponent> INSTANCE =
			new ClassIntPairManager<EntityComponent>();

	/**
	 * Gets the current pair manager INSTANCE being used by this entity
	 * component manager.
	 * 
	 * @return the pair manager
	 */
	public static ClassIntPairManager<EntityComponent> getInstance() {
		return INSTANCE;
	}

	static {
		INSTANCE.registerPair(HealthComponent.class);
		INSTANCE.registerPair(ItemLinkageComponent.class);
		INSTANCE.trimMaps();
	}

	/**
	 * Overridden constructor to prevent instances of this class from being
	 * created.
	 */
	private EntityComponentManager() {
	}
}
