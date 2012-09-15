package com.pi.common.game.entity;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

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

	private IDAllocator idAllocator = new IDAllocator();

	protected abstract E createEntityContainer(final Entity entity);

	public Entity spawnEntity(EntityDef def, Location l) {
		Entity ent = new Entity(this, idAllocator.checkOut(), def.getDefID());
		ent.setLocation(l);
		for (EntityDefComponent d : def.getComponents()) {
			EntityComponent dC = d.createDefaultComponent();
			if (dC != null) {
				ent.addEntityComponent(dC);
			}
		}
		entityMap.set(ent.getID(), createEntityContainer(ent));
		return ent;
	}

	public Entity spawnEntity(int def, Location l, EntityComponent... comps) {
		Entity ent = new Entity(this, idAllocator.checkOut(), def);
		ent.setLocation(l);
		for (EntityComponent c : comps) {
			if (c != null) {
				ent.addEntityComponent(c);
			}
		}
		entityMap.set(ent.getID(), createEntityContainer(ent));
		return ent;
	}

	public Entity forceSpawnEntity(int entityID, int def, Location l,
			EntityComponent... comps) {
		E curr = entityMap.get(entityID);
		if (curr != null) {
			curr.getWrappedEntity().checkIn();
		}
		Entity ent = new Entity(this, entityID, def);
		ent.setLocation(l);
		for (EntityComponent c : comps) {
			if (c != null) {
				ent.addEntityComponent(c);
			}
		}
		entityMap.set(entityID, createEntityContainer(ent));
		return ent;
	}

	public E deRegisterEntity(int id) {
		E ent = entityMap.remove(id);
		if (ent != null) {
			idAllocator.checkIn(id);
			ent.getWrappedEntity().checkIn();
		}
		return ent;
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

	public E getEntityContainer(int id) {
		return entityMap.get(id);
	}

	public Entity getEntity(int id) {
		E container = getEntityContainer(id);
		if (container != null) {
			return container.getWrappedEntity();
		}
		return null;
	}

	public E deRegisterEntity(Entity e) {
		return deRegisterEntity(e.getID());
	}

	public final Iterator<E> getEntitiesWithType(
			final Class<? extends EntityComponent> comp) {
		return new FilteredIterator<E>(getEntities(), new Filter<E>() {
			@Override
			public boolean accept(final E e) {
				return e.getWrappedEntity().getComponent(comp) != null;
			}
		});
	}

	public final Iterator<E> getEntitiesWithType(final int... compIDs) {
		return new FilteredIterator<E>(getEntities(), new Filter<E>() {
			@Override
			public boolean accept(final E e) {
				for (int id : compIDs) {
					if (e.getWrappedEntity().getComponent(id) == null) {
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

	protected ObjectHeap<E> getEntityMap() {
		return entityMap;
	}

	private static class IDAllocator {
		private Queue<Integer> availableIDs = new LinkedBlockingQueue<Integer>();
		private int ID = 0;

		public int checkOut() {
			if (availableIDs.size() > 0) {
				return availableIDs.poll();
			}
			return ID++;
		}

		public void checkIn(int id) {
			if (id == ID - 1) {
				ID--;
			} else {
				availableIDs.add(id);
			}
		}
	}
}
