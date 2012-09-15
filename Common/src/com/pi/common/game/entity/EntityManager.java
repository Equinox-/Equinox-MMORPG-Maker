package com.pi.common.game.entity;

import java.util.Iterator;

import com.pi.common.IDAllocator;
import com.pi.common.database.Location;
import com.pi.common.database.def.entity.EntityDef;
import com.pi.common.database.def.entity.EntityDefComponent;
import com.pi.common.database.world.SectorLocation;
import com.pi.common.game.Filter;
import com.pi.common.game.FilteredIterator;
import com.pi.common.game.ObjectHeap;
import com.pi.common.game.entity.comp.EntityComponent;

public abstract class EntityManager<E extends EntityContainer> {
	/**
	 * The map of registered entities.
	 */
	private final ObjectHeap<E> entityMap = new ObjectHeap<E>();

	/**
	 * The ID Allocator object used to handle entity spawning, and also entity
	 * disposing.
	 */
	private IDAllocator idAllocator = new IDAllocator();

	/**
	 * Creates a new entity container to store the provided entity.
	 * 
	 * @param entity
	 *            the entity to store
	 * @return an entity container with the provided entity
	 */
	protected abstract E createEntityContainer(final Entity entity);

	/**
	 * Spawns an entity specified by the definition at the specified location.
	 * 
	 * More specifically, this first creates a new entity with an ID obtained
	 * from the {@link #idAllocator} and the given definition's ID number. It
	 * then sets the entity's location, and loops through all of the defintion's
	 * components, and attempts to add the {@link EntityComponent} they produce
	 * to the created entity. The last thing it does is store the entity
	 * container created by {@link #createEntityContainer(Entity)} in this
	 * manager and return.
	 * 
	 * @param def
	 *            the definition to use when creating the entity
	 * @param l
	 *            the location to spawn the entity at
	 * @return the entity that was spawned.
	 */
	public Entity spawnEntity(EntityDef def, Location l) {
		Entity ent = new Entity(idAllocator.checkOut(), def.getDefID());
		ent.setLocation(l);
		for (EntityDefComponent d : def.getComponents()) {
			EntityComponent dC = d.createDefaultComponent();
			if (dC != null) {
				ent.addEntityComponent(dC);
			}
		}
		entityMap.set(ent.getEntityID(), createEntityContainer(ent));
		return ent;
	}

	/**
	 * Spawns an entity specified by the definition ID at the specified
	 * location, with the provided entity components.
	 * 
	 * More specifically, this first creates a new entity with an ID obtained
	 * from the {@link #idAllocator} and the given definition ID number. It then
	 * sets the entity's location, and loops through all of the provided
	 * components, and attempts to add them to the created entity. The last
	 * thing it does is store the entity container created by
	 * {@link #createEntityContainer(Entity)} in this manager and return.
	 * 
	 * @param def
	 *            the definition ID to use when creating the entity
	 * @param l
	 *            the location to spawn the entity at
	 * @param comps
	 *            the entity components to spawn the entity with
	 * @return the entity that was spawned.
	 */
	public Entity spawnEntity(int def, Location l, EntityComponent... comps) {
		Entity ent = new Entity(idAllocator.checkOut(), def);
		ent.setLocation(l);
		for (EntityComponent c : comps) {
			if (c != null) {
				ent.addEntityComponent(c);
			}
		}
		entityMap.set(ent.getEntityID(), createEntityContainer(ent));
		return ent;
	}

	/**
	 * Forces an entity to spawn with the given entity ID, definition, location,
	 * and components.
	 * 
	 * More specifically, this first checks to see if the entity is already
	 * stored for the given ID number and disposes it. It then creates a new
	 * entity with the given ID and definition ID number. After doing so it sets
	 * the entity's location, and loops through all of the provided components,
	 * and attempts to add them to the created entity. The last thing it does is
	 * store the entity container created by
	 * {@link #createEntityContainer(Entity)} in this manager and return.
	 * 
	 * @param entityID
	 *            the entity ID to force this entity to have
	 * @param def
	 *            the definition ID to use when creating the entity
	 * @param l
	 *            the location to spawn the entity at
	 * @param comps
	 *            the entity components to spawn the entity with
	 * @return the entity that was spawned.
	 */
	public Entity forceSpawnEntity(int entityID, int def, Location l,
			EntityComponent... comps) {
		E curr = entityMap.get(entityID);
		if (curr != null) {
			curr.getWrappedEntity().checkIn();
		}
		Entity ent = new Entity(entityID, def);
		ent.setLocation(l);
		for (EntityComponent c : comps) {
			if (c != null) {
				ent.addEntityComponent(c);
			}
		}
		entityMap.set(entityID, createEntityContainer(ent));
		return ent;
	}

	/**
	 * Removes the entity with the given ID from the data map, checks it's ID
	 * into the {@link #idAllocator}, and also changes the entity's ID to
	 * <code>-1</code> to signify it is no longer registered.
	 * 
	 * @param id
	 *            the entity ID to dispose
	 * @return the container of the disposed entity, or <code>null</code> if no
	 *         entity was disposed
	 */
	public E deRegisterEntity(int id) {
		E ent = entityMap.remove(id);
		if (ent != null) {
			idAllocator.checkIn(id);
			ent.getWrappedEntity().checkIn();
		}
		return ent;
	}

	/**
	 * Removes the given entity from the data map, checks it's ID into the
	 * {@link #idAllocator}, and also changes the entity's ID to <code>-1</code>
	 * to signify it is no longer registered.
	 * 
	 * @param e
	 *            the entity to dispose
	 * @return the container of the disposed entity, or <code>null</code> if no
	 *         entity was disposed
	 * @see #deRegisterEntity(int)
	 */
	public E deRegisterEntity(Entity e) {
		return deRegisterEntity(e.getEntityID());
	}

	/**
	 * Checks if there is an entity by the provided ID registered with this
	 * entity manager.
	 * 
	 * @param id
	 *            the entity id
	 * @return if the entity is registered
	 */
	public final boolean isEntityRegistered(final int id) {
		return entityMap.get(id) != null;
	}

	/**
	 * Gets the entity container registered with the given ID number.
	 * 
	 * @param id
	 *            the ID number to fetch
	 * @return the entity container, or <code>null</code> if none exists
	 */
	public E getEntityContainer(int id) {
		return entityMap.get(id);
	}

	/**
	 * Gets the entity registered with the given ID number.
	 * 
	 * @param id
	 *            the ID number to fetch
	 * @return the entity, or <code>null</code> if none exists
	 */
	public Entity getEntity(int id) {
		E container = getEntityContainer(id);
		if (container != null) {
			return container.getWrappedEntity();
		}
		return null;
	}

	/**
	 * Creates a filtered iterator that will provide only entities that have the
	 * given component stored in them.
	 * 
	 * @param comp
	 *            the component to require
	 * @return a filtered iterator
	 */
	public final Iterator<E> getEntitiesWithType(
			final Class<? extends EntityComponent> comp) {
		return new FilteredIterator<E>(getEntities(), new Filter<E>() {
			@Override
			public boolean accept(final E e) {
				return e.getWrappedEntity().getComponent(comp) != null;
			}
		});
	}

	/**
	 * Creates a filtered iterator that will provide only entities that have all
	 * the given components stored in them.
	 * 
	 * @param comps
	 *            the components to require
	 * @return a filtered iterator
	 */
	public final Iterator<E> getEntitiesWithType(
			final Class<? extends EntityComponent>... comps) {
		return new FilteredIterator<E>(getEntities(), new Filter<E>() {
			@Override
			public boolean accept(final E e) {
				for (Class<? extends EntityComponent> comp : comps) {
					if (e.getWrappedEntity().getComponent(comp) == null) {
						return false;
					}
				}
				return true;
			}
		});
	}

	/**
	 * Gets all the entities if the given sector.
	 * 
	 * @param loc
	 *            the sector location
	 * @return the entities if the given sector
	 */
	public final Iterator<E> getEntitiesInSector(final SectorLocation loc) {
		return new FilteredIterator<E>(getEntities(), new Filter<E>() {
			@Override
			public boolean accept(final E e) {
				return loc.containsLocation(e.getWrappedEntity());
			}
		});
	}

	/**
	 * Gets all the entities at the given location.
	 * 
	 * @param loc
	 *            the location to scan
	 * @return the entities at the given location
	 */
	public final Iterator<E> getEntitiesAtLocation(final Location loc) {
		return new FilteredIterator<E>(getEntities(), new Filter<E>() {
			@Override
			public boolean accept(final E e) {
				return loc.equals(e.getWrappedEntity());
			}
		});
	}

	/**
	 * Gets all the entities within the specified distance of the given
	 * location.
	 * 
	 * @param l
	 *            the base location
	 * @param maxDist
	 *            the maximum distance
	 * @return the entities within the given distance
	 */
	public final Iterator<E> getEntitiesWithin(final Location l,
			final int maxDist) {
		return new FilteredIterator<E>(getEntities(), new Filter<E>() {
			@Override
			public boolean accept(final E e) {
				return Location.dist(l, e.getWrappedEntity()) <= maxDist;
			}
		});
	}

	/**
	 * Gets the number of registered entities for this entity manager.
	 * 
	 * @return the number of registered entities
	 */
	public final int entityCount() {
		return entityMap.numElements();
	}

	/**
	 * Gets an iterator instance that cycles through all the entities registered
	 * to this manager.
	 * 
	 * @return the iterator
	 */
	public final Iterator<E> getEntities() {
		return entityMap.iterator();
	}

	/**
	 * The entity storage heap.
	 */
	protected ObjectHeap<E> getEntityMap() {
		return entityMap;
	}
}
