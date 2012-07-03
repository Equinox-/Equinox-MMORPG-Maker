package com.pi.common.game;

/**
 * An enum representing the possible entity types.
 * 
 * @author Westin
 * 
 */
public enum EntityType {
	/**
	 * Normal entity type.
	 */
	Normal(Entity.class),
	/**
	 * Living entity type.
	 */
	Living(LivingEntity.class),
	/**
	 * Entity with combat abilities.
	 */
	Combat(CombatEntity.class);

	/**
	 * The class describing this entity.
	 */
	private Class<? extends Entity> clazz;

	/**
	 * Creates an entity type enum with the given class.
	 * 
	 * @param sClazz the class
	 */
	private EntityType(final Class<? extends Entity> sClazz) {
		this.clazz = sClazz;
	}

	/**
	 * Gets the class describing this entity.
	 * 
	 * @return the entity class
	 */
	public Class<? extends Entity> getEntityClass() {
		return clazz;
	}

	/**
	 * Gets the entity type of the given entity.
	 * 
	 * @param entity the entity
	 * @return the entity type
	 */
	public static EntityType getEntityType(final Entity entity) {
		for (EntityType e : EntityType.values()) {
			if (e.getEntityClass().getName()
					.equals(entity.getClass().getName())) {
				return e;
			}
		}
		return null;
	}
}
