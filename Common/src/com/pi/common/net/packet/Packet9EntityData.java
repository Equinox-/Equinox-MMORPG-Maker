package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.database.Location;
import com.pi.common.database.world.TileLayer;
import com.pi.common.game.entity.Entity;
import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.common.game.entity.comp.EntityComponentManager;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.util.ObjectHeap;

/**
 * A packet sent by the server to the client to initially create an entity on
 * the client.
 * 
 * @author Westin
 * 
 */
public class Packet9EntityData extends Packet {
	public Location loc;
	public TileLayer layer;
	public int defID;
	public int entID;
	public ObjectHeap<EntityComponent> components =
			new ObjectHeap<EntityComponent>(
					EntityComponentManager.getInstance()
							.getPairCount(), true);

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entID);
		if (loc == null) {
			loc = new Location();
		}

		loc.writeData(pOut);
		pOut.writeInt(layer.ordinal());
		pOut.writeInt(defID);

		EntityComponent comp;
		for (int i = 0; i < EntityComponentManager.getInstance()
				.getPairCount(); i++) {
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
		entID = pIn.readInt();
		if (loc == null) {
			loc = new Location();
		}

		loc.readData(pIn);
		int lI = pIn.readInt();
		if (lI >= 0 && lI < TileLayer.MAX_VALUE.ordinal()) {
			layer = TileLayer.values()[lI];
		} else {
			layer = TileLayer.MASK1;
		}
		defID = pIn.readInt();

		for (int i = 0; i < EntityComponentManager.getInstance()
				.getPairCount(); i++) {
			if (pIn.readByte() == 1) {
				try {
					Class<? extends EntityComponent> clazz =
							EntityComponentManager.getInstance()
									.getPairClass(i);
					EntityComponent comp = clazz.newInstance();
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

	/**
	 * Create an instance of the entity data packet for the given entity.
	 * 
	 * @param e the entity to create a packet for
	 * @return the packet instance
	 */
	public static Packet9EntityData create(final Entity e) {
		Packet9EntityData pack = new Packet9EntityData();
		pack.defID = e.getEntityDef();
		pack.entID = e.getEntityID();
		pack.layer = e.getLayer();
		pack.loc = e;
		pack.components = e.getComponents();
		return pack;
	}

	@Override
	public final int getLength() {
		if (loc == null) {
			loc = new Location();
		}
		int size =
				(3 * SizeOf.INT) + loc.getLength()
						+ (SizeOf.BYTE * components.capacity());
		EntityComponent comp;
		for (int i = 0; i < components.capacity(); i++) {
			comp = components.get(i);
			if (comp != null) {
				size += comp.getLength();
			}
		}
		return size;
	}
}
