package com.pi.common.database.def;

import java.io.IOException;

import com.pi.common.contants.EntityConstants;
import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.database.GraphicsObject;
import com.pi.common.game.entity.Entity;
import com.pi.common.game.entity.EntityType;
import com.pi.common.game.entity.LivingEntity;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * The class representing an entity's definition.
 * 
 * @author Westin
 * 
 */
public class EntityDef extends GraphicsObject {
	/**
	 * This definintion's identification number.
	 */
	private int defID = -1;
	/**
	 * The number of horizontal frames for the movement animation.
	 */
	private int horizFrames = 4;
	/**
	 * The logic class name.
	 */
	private String logicClass;

	/**
	 * The entity type this definition uses.
	 */
	private EntityType eType = EntityType.Normal;

	/**
	 * The maximum health of this entity. This is only set, or able to be set if
	 * the entity type is a sub type of the living entity type.
	 */
	private int maximumHealth =
			EntityConstants.DEFAULT_MAXIMUM_HEALTH;

	/**
	 * The amount of time in milliseconds for an entity to attack something.
	 */
	private long attackSpeed =
			EntityConstants.DEFAULT_ENTITY_ATTACK_SPEED;

	/**
	 * Creates an entity definition with the given identification number.
	 * 
	 * @param eDefID the definition id
	 */
	public EntityDef(final int eDefID) {
		this.defID = eDefID;
	}

	/**
	 * Gets the number of horizontal frames for the movement animation.
	 * 
	 * @return the horizontal frame count
	 */
	public final int getHorizontalFrames() {
		return horizFrames;
	}

	/**
	 * Sets the number of horizontal frames for the movement animation.
	 * 
	 * @param f the number of horizontal frames
	 */
	public final void setHorizontalFrames(final int f) {
		this.horizFrames = f;
	}

	/**
	 * Gets the definition ID used by this definition.
	 * 
	 * @return the definition id
	 */
	public final int getDefID() {
		return defID;
	}

	/**
	 * Sets this definition's logic class.
	 * 
	 * @param clazz the new logic class
	 */
	public final void setLogicClass(final String clazz) {
		this.logicClass = clazz;
	}

	/**
	 * Gets the logic class for this entity definition.
	 * 
	 * @return the logic class
	 */
	public final String getLogicCLass() {
		return logicClass;
	}

	/**
	 * Gets this entity definition's type.
	 * 
	 * @return the entity type
	 */
	public final EntityType getEntityType() {
		return eType;
	}

	/**
	 * Sets the entity type for this definition.
	 * 
	 * @param sEType the entity type
	 */
	public final void setEntityType(final EntityType sEType) {
		this.eType = sEType;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		super.writeData(pOut);
		pOut.writeInt(horizFrames);
		pOut.writeString(logicClass);
		pOut.writeInt(eType.ordinal());
		if (eType.isSubtype(EntityType.Living)) {
			pOut.writeInt(maximumHealth);
		}
		if (eType.isSubtype(EntityType.Combat)) {
			pOut.writeLong(attackSpeed);
		}
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		super.readData(pIn);
		horizFrames = pIn.readInt();
		logicClass = pIn.readString();
		eType = EntityType.values()[pIn.readInt()];
		if (eType.isSubtype(EntityType.Living)) {
			maximumHealth = pIn.readInt();
		}
		if (eType.isSubtype(EntityType.Combat)) {
			attackSpeed = pIn.readLong();
		}
	}

	@Override
	public final int getLength() {
		int length =
				super.getLength()
						+ (2 * SizeOf.INT)
						+ PacketOutputStream
								.stringByteLength(logicClass);
		if (eType.isSubtype(EntityType.Living)) {
			length += SizeOf.INT; // Max Health
		}
		if (eType.isSubtype(EntityType.Combat)) {
			length += SizeOf.LONG;
		}
		return length;
	}

	/**
	 * Gets maximum health of this entity.
	 * 
	 * @return the maximum health
	 * @throws UnsupportedOperationException if this entity definition's type
	 *             isn't a sub-type of the Living entity type
	 */
	public final int getMaximumHealth() {
		if (eType.isSubtype(EntityType.Living)) {
			return maximumHealth;
		} else {
			throw new UnsupportedOperationException(
					"Unable to get the maximum health of an entity definition with a type that isn't a subtype of the Living entity type");
		}
	}

	/**
	 * Sets maximum health of this entity.
	 * 
	 * @param sMaximumHealth the maximum health of this entity
	 * @throws UnsupportedOperationException if this entity definition's type
	 *             isn't a sub-type of the Living entity type
	 */
	public final void setMaximumHealth(final int sMaximumHealth) {
		if (eType.isSubtype(EntityType.Living)) {
			this.maximumHealth = sMaximumHealth;
		} else {
			throw new UnsupportedOperationException(
					"Unable to set the maximum health of an entity definition with a type that isn't a subtype of the Living entity type");
		}
	}

	/**
	 * Gets maximum health of this entity.
	 * 
	 * @return the maximum health
	 * @throws UnsupportedOperationException if this entity definition's type
	 *             isn't a sub-type of the Combat entity type
	 */
	public final long getAttackSpeed() {
		if (eType.isSubtype(EntityType.Combat)) {
			return attackSpeed;
		} else {
			throw new UnsupportedOperationException(
					"Unable to get the attack speed of an entity definition with a type that isn't a subtype of the Combat entity type");
		}
	}

	/**
	 * Sets maximum health of this entity.
	 * 
	 * @param sAttackSpeed the attack speed in milliseconds for this entity
	 * @throws UnsupportedOperationException if this entity definition's type
	 *             isn't a sub-type of the Combat entity type
	 */
	public final void setAttackSpeed(final long sAttackSpeed) {
		if (eType.isSubtype(EntityType.Combat)) {
			this.attackSpeed = sAttackSpeed;
		} else {
			throw new UnsupportedOperationException(
					"Unable to set the attack speed of an entity definition with a type that isn't a subtype of the Combat entity type");
		}
	}

	/**
	 * Creates an entity instance of the proper type and variables for this
	 * entity definition.
	 * 
	 * @return the created entity
	 */
	public final Entity createEntityInstance() {
		Entity ent = eType.createInstance();
		ent.setEntityDef(defID);
		if (ent instanceof LivingEntity) {
			((LivingEntity) ent).setHealth(maximumHealth);
		}
		return ent;

	}
}
