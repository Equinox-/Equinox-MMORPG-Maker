package com.pi.common.net.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.database.Location;
import com.pi.common.database.world.TileLayer;
import com.pi.common.game.entity.Entity;
import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.common.game.entity.comp.EntityComponentType;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

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
	public List<EntityComponent> components = new ArrayList<EntityComponent>();

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

		pOut.writeInt(components.size());
		EntityComponent comp;
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

		int size = pIn.readInt();
		components.clear();
		for (int i = 0; i < size; i++) {
			components.add(null);
			if (pIn.readByte() == 1) {
				try {
					Class<? extends EntityComponent> clazz = EntityComponentType
							.getComponentClass(i);
					EntityComponent comp = clazz.newInstance();
					components.set(i, comp);
					comp.readData(pIn);
				} catch (Exception e) {
					throw new IOException("Unable to create a component: "
							+ e.toString());
				}
			}
		}
	}

	/**
	 * Create an instance of the entity data packet for the given entity.
	 * 
	 * @param e
	 *            the entity to create a packet for
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
		int size = (4 * SizeOf.INT) + loc.getLength()
				+ (SizeOf.BYTE * components.size());
		EntityComponent comp;
		for (int i = 0; i < components.size(); i++) {
			comp = components.get(i);
			if (comp != null) {
				size += comp.getLength();
			}
		}
		return size;
	}
}
