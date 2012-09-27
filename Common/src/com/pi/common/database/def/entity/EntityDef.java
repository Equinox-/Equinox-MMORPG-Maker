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
	 * @param eDefID the definition id
	 */
	public EntityDef(final int eDefID) {
		this.defID = eDefID;
		components =
				new ObjectHeap<EntityDefComponent>(
						EntityDefComponentManager.getInstance()
								.getPairCount(), true);
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

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		super.writeData(pOut);
		pOut.writeInt(horizFrames);
		EntityDefComponent comp;
		for (int i = 0; i < EntityDefComponentManager
				.getInstance().getPairCount(); i++) {
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
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		super.readData(pIn);
		horizFrames = pIn.readInt();

		for (int i = 0; i < EntityDefComponentManager
				.getInstance().getPairCount(); i++) {
			if (pIn.readByte() == 1) {
				try {
					Class<? extends EntityDefComponent> clazz =
							EntityDefComponentManager
									.getInstance().getPairClass(
											i);
					EntityDefComponent comp =
							clazz.newInstance();
					components.set(i, comp);
					comp.readData(pIn);
				} catch (Exception e) {
					throw new IOException(
							"Unable to create a component: "
									+ e.toString());
				}
			} else {
				components.set(i, null);
			}
		}
	}

	@Override
	public final int getLength() {
		int size =
				super.getLength()
						+ SizeOf.INT
						+ (SizeOf.BYTE * EntityDefComponentManager
								.getInstance().getPairCount());
		EntityDefComponent comp;
		for (int i = 0; i < EntityDefComponentManager
				.getInstance().getPairCount(); i++) {
			comp = components.get(i);
			if (comp != null) {
				size += comp.getLength();
			}
		}
		return size;
	}

	/**
	 * Gets one of this definiton's components by id, and returns the component
	 * instance, or <code>null</code> if the component type isn't registered
	 * with this definition.
	 * 
	 * @param id the component ID to fetch
	 * @return the component, or <code>null</code>
	 */
	public final EntityDefComponent getComponent(final int id) {
		return components.get(id);
	}

	/**
	 * Gets one of this definiton's components by class, and returns the
	 * component instance, or <code>null</code> if the component type isn't
	 * registered with this definition.
	 * 
	 * @param clazz the class to fetch
	 * @return the component, or <code>null</code>
	 */
	public final EntityDefComponent getComponent(
			final Class<? extends EntityDefComponent> clazz) {
		int id =
				EntityDefComponentManager.getInstance()
						.getPairID(clazz);
		return getComponent(id);
	}

	/**
	 * Adds all the given entity definition components to the component heap.
	 * 
	 * @param comps the components to add
	 */
	public final void addEntityDefComponents(
			final EntityDefComponent[] comps) {
		for (EntityDefComponent c : comps) {
			addEntityDefComponent(c);
		}
	}

	/**
	 * Adds the given entity definition component to the component heap.
	 * 
	 * @param comp the component to add
	 */
	public final void addEntityDefComponent(
			final EntityDefComponent comp) {
		int id =
				EntityDefComponentManager.getInstance()
						.getPairID(comp.getClass());
		if (id >= 0) {
			components.set(id, comp);
		}
	}

	/**
	 * Gets the data heap representing the components registered to this
	 * definition.
	 * 
	 * @return the data heap
	 */
	public final ObjectHeap<EntityDefComponent> getComponents() {
		return components;
	}

	/**
	 * Sets the current definiton's components by first clearing the list, then
	 * adding the provided components.
	 * 
	 * @param nComponents the components to change to
	 */
	public final void setComponents(
			final EntityDefComponent[] nComponents) {
		for (int i = 0; i < EntityDefComponentManager
				.getInstance().getPairCount(); i++) {
			if (i < nComponents.length) {
				this.components.set(i, nComponents[i]);
			} else {
				this.components.set(i, null);
			}
		}
	}

	/**
	 * Sets the current definiton's components by first clearing the list, then
	 * adding the provided components.
	 * 
	 * @param nComponents the components to change to
	 */
	public final void setComponents(
			final ObjectHeap<EntityDefComponent> nComponents) {
		for (int i = 0; i < EntityDefComponentManager
				.getInstance().getPairCount(); i++) {
			if (i < nComponents.capacity()) {
				this.components.set(i, nComponents.get(i));
			} else {
				this.components.set(i, null);
			}
		}
	}
}
