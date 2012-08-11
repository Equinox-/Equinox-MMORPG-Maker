package com.pi.common.game.entity;

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
	Combat(CombatEntity.class),
	/**
	 * Entity that represents an item on the map.
	 */
	Item(ItemEntity.class);

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
	 * Returns a new instance of the entity with this type, or <code>null</code>
	 * if unable to comply.
	 * 
	 * @return the entity instance
	 */
	public Entity createInstance() {
		try {
			return getEntityClass().newInstance();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Checks if this entity type is the sub type of another entity type. This
	 * returns <code>true</code> if an entity instance created by this type is
	 * an instance of an instance created by the entity type in the parameter.
	 * 
	 * @param superType the super type to check
	 * @return if this type is a sub type of the given type
	 */
	public boolean isSubtype(final EntityType superType) {
		return superType.clazz.isAssignableFrom(clazz);
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
