package com.pi.common.game.entity;

import java.util.ArrayList;

import com.pi.common.contants.Direction;
import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileFlags;
import com.pi.common.database.Location;
import com.pi.common.database.world.Sector;
import com.pi.common.database.world.Tile;
import com.pi.common.database.world.TileLayer;
import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.common.game.entity.comp.EntityComponentType;
import com.pi.common.world.SectorManager;

/**
 * Entity description class, for describing an in game entity.
 * 
 * @author Westin
 */
public class Entity extends Location {
	/**
	 * The direction this entity is facing in.
	 */
	private Direction dir = Direction.UP;
	/**
	 * The tile layer that this entity resides above.
	 */
	private TileLayer aboveLayer = TileLayer.MASK1;
	/**
	 * The entity identification number currently in use.
	 */
	private int entityID = -1;
	/**
	 * The entity definition identification number this entity uses.
	 */
	private int defID = 0;

	/**
	 * The extra entity data this entity contains.
	 */
	private ArrayList<EntityComponent> components;
	/**
	 * The entity manager responsible for this entity.
	 */
	private final EntityManager<? extends EntityContainer> manager;

	/**
	 * Create an entity with the given definition.
	 * 
	 * @param def
	 *            the definition to use.
	 * @param eID
	 *            the entity ID
	 * @param mgr
	 *            the entity manager responsible for this entity
	 */
	Entity(final EntityManager<? extends EntityContainer> mgr, final int eID,
			final int def) {
		this(mgr, eID);
		this.defID = def;
	}

	/**
	 * Creates an entity with the default values.
	 * 
	 * @param eID
	 *            the entity ID
	 * @param mgr
	 *            the entity manager responsible for this entity
	 */
	Entity(final EntityManager<? extends EntityContainer> mgr, final int eID) {
		this.entityID = eID;
		this.manager = mgr;
		this.components = new ArrayList<EntityComponent>(0);
	}

	/**
	 * Sets the entity definition to the provided value.
	 * 
	 * @param entityDef
	 *            the definition to use
	 */
	public final void setEntityDef(final int entityDef) {
		this.defID = entityDef;
	}

	/**
	 * Gets the entity definition that is being used by this entity.
	 * 
	 * @return the entity definition.
	 */
	public final int getEntityDef() {
		return this.defID;
	}

	/**
	 * Gets this entity's current direction.
	 * 
	 * @return the current direction
	 */
	public final Direction getDir() {
		return dir;
	}

	/**
	 * Gets this entity's assigned identification number.
	 * 
	 * @return this entity's identification number or <code>-1</code> if
	 *         unassigned
	 */
	public final int getEntityID() {
		return entityID;
	}

	/**
	 * Gets the tile layer that this entity currently resides above.
	 * 
	 * @return the tile layer
	 */
	public final TileLayer getLayer() {
		return aboveLayer;
	}

	/**
	 * Sets the location of this entity.
	 * 
	 * @param l
	 *            the new location
	 */
	public final void setLocation(final Location l) {
		setLocation(l.x, l.plane, l.z);
	}

	/**
	 * Sets the tile layer that this entity will reside above.
	 * 
	 * @param t
	 *            the new tile layer
	 */
	public final void setLayer(final TileLayer t) {
		aboveLayer = t;
	}

	/**
	 * Sets the new direction for this entity.
	 * 
	 * @param sDir
	 *            the new direction
	 */
	public final void setDir(final Direction sDir) {
		this.dir = sDir;
	}

	/**
	 * On calling this method the entity will forcefully move one step in the
	 * current direction it is facing.
	 */
	public final void doMovement() {
		x += dir.getXOff();
		z += dir.getZOff();
	}

	/**
	 * Does a short teleport to another location, changing this entity's
	 * direction to the best fitting direction.
	 * 
	 * @param apply
	 *            the new location
	 */
	public final void teleportShort(final Location apply) {
		// calculate dir
		int xC = apply.x - x;
		int zC = apply.z - z;
		if (xC != 0 || zC != 0) {
			dir = Direction.getBestDirection(xC, zC);
		}
		this.x = apply.x;
		this.z = apply.z;
	}

	/**
	 * Checks if this entity can move in the specified direction, using the
	 * provided sector manager to get sector information.
	 * 
	 * @param mgr
	 *            the sector manager to use
	 * @param sDir
	 *            the direction to check movement in
	 * @return <code>true</code> if the entity can move in the specified
	 *         direction, and won't be moving into an empty sector. Otherwise,
	 *         <code>false</code>.
	 */
	public final boolean canMoveIn(final SectorManager mgr, final Direction sDir) {
		Sector sec = mgr.getSector(getSectorX(), getPlane(), getSectorZ());
		if (sec != null) {
			int moveSX = SectorConstants.worldToSectorX(x + sDir.getXOff());
			int moveSZ = SectorConstants.worldToSectorZ(z + sDir.getZOff());
			Sector move;
			if (moveSX != sec.getSectorX() || moveSZ != sec.getSectorZ()) {
				move = mgr.getSector(moveSX, getPlane(), moveSZ);
			} else {
				move = sec;
			}
			if (move != null) {
				Tile t = sec.getGlobalTile(x, z);
				if (t != null) {
					switch (sDir) {
					case UP:
						return !t.hasFlag(TileFlags.WALL_NORTH);
					case DOWN:
						return !t.hasFlag(TileFlags.WALL_SOUTH);
					case RIGHT:
						return !t.hasFlag(TileFlags.WALL_EAST);
					case LEFT:
						return !t.hasFlag(TileFlags.WALL_WEST);
					default:
						break;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if this entity can move in it's currently facing direction, using
	 * the provided sector manager to get sector information.
	 * 
	 * @see Entity#canMoveIn(SectorManager, Direction)
	 * @param sec
	 *            the sector manager
	 * @return <code>true</code> if the entity can move in the specified
	 *         direction, and won't be moving into an empty sector. Otherwise,
	 *         <code>false</code>.
	 */
	public final boolean canMove(final SectorManager sec) {
		return canMoveIn(sec, getDir());
	}

	public EntityComponent getComponent(int id) {
		if (id >= 0 && id < components.size()) {
			return components.get(id);
		}
		return null;
	}

	public EntityComponent getComponent(Class<? extends EntityComponent> clazz) {
		int id = EntityComponentType.getComponentID(clazz);
		return getComponent(id);
	}

	private void ensureCapacity(int i) {
		components.ensureCapacity(i);
		while (components.size() < i) {
			components.add(null);
		}
	}

	public void addEntityComponents(EntityComponent[] comps) {
		for (EntityComponent c : comps) {
			addEntityComponent(c);
		}
	}

	public void addEntityComponent(EntityComponent comp) {
		int id = EntityComponentType.getComponentID(comp.getClass());
		if (id >= 0) {
			ensureCapacity(id + 1);
			components.set(id, comp);
		}
	}

	public int getID() {
		return entityID;
	}

	void checkIn() {
		entityID = -1;
	}

	public ArrayList<EntityComponent> getComponents() {
		return components;
	}

	public void setComponents(EntityComponent[] components) {
		this.components.clear();
		addEntityComponents(components);
	}
}
