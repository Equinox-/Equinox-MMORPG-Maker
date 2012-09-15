package com.pi.common.database.def.entity;

import java.io.IOException;
import java.util.ArrayList;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.database.GraphicsObject;
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
	 * The extra entity data this entity definition contains.
	 */
	private ArrayList<EntityDefComponent> components;

	/**
	 * Creates an entity definition with the given identification number.
	 * 
	 * @param eDefID
	 *            the definition id
	 */
	public EntityDef(final int eDefID) {
		this.defID = eDefID;
		components = new ArrayList<EntityDefComponent>(0);
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
		pOut.writeInt(components.size());
		EntityDefComponent comp;
		for (int i = 0; i < components.size(); i++) {
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

		int size = pIn.readInt();
		components.clear();
		for (int i = 0; i < size; i++) {
			if (pIn.readByte() == 1) {
				components.add(null);
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
			}
		}
	}

	@Override
	public final int getLength() {
		int size = super.getLength() + (2 * SizeOf.INT)
				+ (SizeOf.BYTE * components.size());
		EntityDefComponent comp;
		for (int i = 0; i < components.size(); i++) {
			comp = components.get(i);
			if (comp != null) {
				size += comp.getLength();
			}
		}
		return size;
	}

	public EntityDefComponent getComponent(int id) {
		if (id >= 0 && id < components.size()) {
			return components.get(id);
		}
		return null;
	}

	public EntityDefComponent getComponent(
			Class<? extends EntityDefComponent> clazz) {
		int id = EntityDefComponentType.getComponentID(clazz);
		return getComponent(id);
	}

	private void ensureCapacity(int i) {
		components.ensureCapacity(i);
		while (components.size() < i) {
			components.add(null);
		}
	}

	public void addEntityDefComponents(EntityDefComponent[] comps) {
		for (EntityDefComponent c : comps) {
			addEntityDefComponent(c);
		}
	}

	public void addEntityDefComponent(EntityDefComponent comp) {
		int id = EntityDefComponentType.getComponentID(comp.getClass());
		if (id >= 0) {
			ensureCapacity(id + 1);
			components.set(id, comp);
		}
	}

	public ArrayList<EntityDefComponent> getComponents() {
		return components;
	}

	public void setComponents(EntityDefComponent[] components) {
		this.components.clear();
		addEntityDefComponents(components);
	}
}
