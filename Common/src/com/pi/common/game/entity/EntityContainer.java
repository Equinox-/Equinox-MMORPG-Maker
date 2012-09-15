package com.pi.common.game.entity;

public abstract class EntityContainer {
	/**
	 * The entity wrapped by this client entity.
	 */
	private final Entity wrapped;

	/**
	 * Creates an entity wrapper that wraps the provided entity.
	 * 
	 * @param e the entity to wrap
	 */
	public EntityContainer(final Entity e) {
		this.wrapped = e;
	}

	/**
	 * Gets the entity that this client entity wraps.
	 * 
	 * @return the wrapped entity
	 */
	public final Entity getWrappedEntity() {
		return wrapped;
	}
}
