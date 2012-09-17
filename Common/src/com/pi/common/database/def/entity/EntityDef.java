package com.pi.common.database.def.entity;

import java.io.IOException;

import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.database.GraphicsObject;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.util.ObjectHeap;

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
	 * The extra entity data this entity definition contains.
	 */
	private ObjectHeap<EntityDefComponent> components;

	/**
	 * Creates an entity definition with the given identification number.
	 * 
	 * @param eDefID
	 *            the definition id
	 */
	public EntityDef(final int eDefID) {
		this.defID = eDefID;
		components = new ObjectHeap<EntityDefComponent>(
				EntityDefComponentType.getComponentCount(), true);
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
	 * @param f
	 *            the number of horizontal frames
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

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		super.writeData(pOut);
		pOut.writeInt(horizFrames);
		EntityDefComponent comp;
		for (int i = 0; i < EntityDefComponentType.getComponentCount(); i++) {
			comp = components.get(i);
			if (comp != null) {
				pOut.writeByte(1);
				comp.writeData(pOut);
			} else {
				pOut.writeByte(0);
			}
		}
	}

	@Override
	public final void readData(final PacketInputStream pIn) throws IOException {
		super.readData(pIn);
		horizFrames = pIn.readInt();

		for (int i = 0; i < EntityDefComponentType.getComponentCount(); i++) {
			if (pIn.readByte() == 1) {
				try {
					Class<? extends EntityDefComponent> clazz = EntityDefComponentType
							.getComponentClass(i);
					EntityDefComponent comp = clazz.newInstance();
					components.set(i, comp);
					comp.readData(pIn);
				} catch (Exception e) {
					throw new IOException("Unable to create a component: "
							+ e.toString());
				}
			} else {
				components.set(i, null);
			}
		}
	}

	@Override
	public final int getLength() {
		int size = super.getLength() + SizeOf.INT
				+ (SizeOf.BYTE * EntityDefComponentType.getComponentCount());
		EntityDefComponent comp;
		for (int i = 0; i < EntityDefComponentType.getComponentCount(); i++) {
			comp = components.get(i);
			if (comp != null) {
				size += comp.getLength();
			}
		}
		return size;
	}

	public EntityDefComponent getComponent(int id) {
		return components.get(id);
	}

	public EntityDefComponent getComponent(
			Class<? extends EntityDefComponent> clazz) {
		int id = EntityDefComponentType.getComponentID(clazz);
		return getComponent(id);
	}

	public void addEntityDefComponents(EntityDefComponent[] comps) {
		for (EntityDefComponent c : comps) {
			addEntityDefComponent(c);
		}
	}

	public void addEntityDefComponent(EntityDefComponent comp) {
		int id = EntityDefComponentType.getComponentID(comp.getClass());
		if (id >= 0) {
			components.set(id, comp);
		}
	}

	public ObjectHeap<EntityDefComponent> getComponents() {
		return components;
	}

	public void setComponents(EntityDefComponent[] components) {
		for (int i = 0; i < EntityDefComponentType.getComponentCount(); i++) {
			this.components.set(i, null);
		}
		addEntityDefComponents(components);
	}
}
