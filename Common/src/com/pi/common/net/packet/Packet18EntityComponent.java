package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.game.entity.Entity;
import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.common.game.entity.comp.EntityComponentType;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * Packet representing a health update for an entity.
 * 
 * @author Westin
 * 
 */
public class Packet18EntityComponent extends Packet {
	/**
	 * The entity this packet updates.
	 */
	public int entityID;
	/**
	 * The new health value.
	 */
	public EntityComponent eComp;

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entityID);
		if (eComp != null) {
			pOut.writeByte((byte) EntityComponentType.getComponentID(eComp
					.getClass()));
			eComp.writeData(pOut);
		} else {
			pOut.writeByte(-1);
		}
	}

	@Override
	public final int getLength() {
		int size = SizeOf.INT + SizeOf.BYTE;
		if (eComp != null) {
			size += eComp.getLength();
		}
		return size;
	}

	@Override
	public final void readData(final PacketInputStream pIn) throws IOException {
		entityID = pIn.readInt();
		byte compID = pIn.readByte();
		if (compID == -1) {
			eComp = null;
		} else {
			try {
				Class<? extends EntityComponent> compClass = EntityComponentType
						.getComponentClass(compID);
				eComp = compClass.newInstance();
				eComp.readData(pIn);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				eComp = null;
			}
		}
	}

	/**
	 * Creates a entity component update for the given entity.
	 * 
	 * @param lE
	 *            the entity
	 * @param component
	 *            the component type to use
	 * @return the created packet
	 */
	public static Packet18EntityComponent create(final Entity lE,
			final Class<? extends EntityComponent> component) {
		Packet18EntityComponent p = new Packet18EntityComponent();
		p.entityID = lE.getEntityID();
		p.eComp = lE.getComponent(component);
		return p;
	}

	/**
	 * Creates a entity component update for the given entity.
	 * 
	 * @param lE
	 *            the entity
	 * @param component
	 *            the component to use
	 * @return the created packet
	 */
	public static Packet18EntityComponent create(final int entity,
			final EntityComponent comp) {
		Packet18EntityComponent p = new Packet18EntityComponent();
		p.entityID = entity;
		p.eComp = comp;
		return p;
	}

}