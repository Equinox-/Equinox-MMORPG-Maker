package com.pi.common.net.packet;

import java.io.IOException;
import java.util.Arrays;

import com.pi.common.debug.PILogger;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public abstract class Packet implements PacketObject, Comparable<Packet> {
	@SuppressWarnings("unchecked")
	public static Class<? extends Packet>[] idMapping = new Class[0];
	public long timeStamp = System.currentTimeMillis();

	static {
		try {
			registerPacket(Packet0Disconnect.class);
			registerPacket(Packet1Login.class);
			registerPacket(Packet2Alert.class);
			registerPacket(Packet3Register.class);
			registerPacket(Packet4Sector.class);
			registerPacket(Packet5SectorRequest.class);
			registerPacket(Packet6BlankSector.class);
			registerPacket(Packet7EntityTeleport.class);
			registerPacket(Packet8EntityDispose.class);
			registerPacket(Packet9EntityData.class);
			registerPacket(Packet10EntityDataRequest.class);
			registerPacket(Packet11LocalEntityID.class);
			registerPacket(Packet12EntityDefRequest.class);
			registerPacket(Packet13EntityDef.class);
			registerPacket(Packet14ClientMove.class);
			registerPacket(Packet15GameState.class);
			registerPacket(Packet16EntityMove.class);
			registerPacket(Packet17Clock.class);
		} catch (Exception e) {
			throw new RuntimeException(e.toString());
		}
	}

	public Packet() {
	}

	public static void registerPacket(Class<? extends Packet> pClass)
			throws InstantiationException, IllegalAccessException {
		int id = pClass.newInstance().getID();
		if (id < idMapping.length && idMapping[id] != null) {
			throw new IllegalArgumentException("Duplicate packet id: " + id);
		} else {
			if (id >= idMapping.length) {
				idMapping = Arrays.copyOf(idMapping.clone(),
						Math.max(id + 1, idMapping.length));
			}
			idMapping[id] = pClass;
		}
	}

	public static Packet getPacket(PILogger log, int id) {
		try {
			if (id >= 0 && id < idMapping.length) {
				Class<? extends Packet> clazz = idMapping[id];
				return clazz != null ? clazz.newInstance() : null;
			} else {
				log.severe("Bad packet ID: " + id);
				return null;
			}
		} catch (Exception e) {
			log.printStackTrace(e);
			log.severe("Skipping packet by id: " + id);
			return null;
		}
	}

	public String getName() {
		return getClass().getSimpleName();
	}

	public static Packet getPacket(PILogger log, PacketInputStream pIn)
			throws IOException {
		int id = pIn.readByte();
		Packet packet = getPacket(log, id);
		if (packet == null)
			throw new IOException("Bad packet id: " + id);
		// packet.timeStamp = pIn.readLong();
		return packet;
	}

	public void writePacket(PacketOutputStream pOut) throws IOException {
		pOut.writeByte(getID());
		// pOut.writeLong(timeStamp);
		writeData(pOut);
	}

	public int getPacketLength() {
		return 1 + getLength();
	}

	public int getID() {
		// This is a janky way to do it.
		char[] name = getClass().getSimpleName().toCharArray();
		boolean numStart = false;
		String num = new String();
		for (int i = 0; i < name.length; i++) {
			if (name[i] >= '0' && name[i] <= '9') {
				numStart = true;
				num += name[i];
			} else if (numStart) {
				break;
			}
		}
		try {
			if (numStart)
				return Integer.valueOf(num);
		} catch (NumberFormatException e) {
		}
		throw new UnsupportedOperationException(
				"The class name doesn't contain ID, please ovveride getID()");
	}

	@Override
	public int compareTo(Packet p) {
		if (p == this || p.timeStamp == timeStamp)
			return 0;
		if (timeStamp < p.timeStamp)
			return -1;
		else
			return 1;
	}
}
