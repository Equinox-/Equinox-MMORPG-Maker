package com.pi.common.net.packet;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

import com.pi.common.debug.PILogger;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public abstract class Packet implements PacketObject {
    @SuppressWarnings("unchecked")
    public static Class<? extends Packet>[] idMapping = new Class[0];
    public final long timeStamp = System.currentTimeMillis();

    static {
	registerPacket(0, Packet0Disconnect.class);
	registerPacket(1, Packet1Login.class);
	registerPacket(2, Packet2Alert.class);
	registerPacket(3, Packet3Register.class);
	registerPacket(4, Packet4Sector.class);
	registerPacket(5, Packet5SectorRequest.class);
	registerPacket(6, Packet6BlankSector.class);
	registerPacket(7, Packet7EntityTeleport.class, true);
	registerPacket(8, Packet8EntityDispose.class);
	registerPacket(9, Packet9EntityData.class, true);
	registerPacket(10, Packet10EntityDataRequest.class);
	registerPacket(11, Packet11LocalEntityID.class);
	registerPacket(12, Packet12EntityDefRequest.class);
	registerPacket(13, Packet13EntityDef.class);
	registerPacket(14, Packet14ClientMove.class);
	registerPacket(15, Packet15GameState.class);
	registerPacket(16, Packet16EntityMove.class);
    }

    public Packet() {
    }

    public static void registerPacket(int id, Class<? extends Packet> pClass) {
	registerPacket(id, pClass, false);
    }

    public static void registerPacket(int id, Class<? extends Packet> pClass,
	    boolean highPriority) {
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
	int id = pIn.readInt();
	Packet packet = getPacket(log, id);
	if (packet == null)
	    throw new IOException("Bad packet id: " + id);
	packet.readData(pIn);
	return packet;
    }

    public void writePacket(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(getID());
	writeData(pOut);
    }

    public int getPacketLength() {
	return 4 + getLength();
    }

    public abstract int getID();
}
