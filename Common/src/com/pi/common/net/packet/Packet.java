package com.pi.common.net.packet;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public abstract class Packet {
    @SuppressWarnings("unchecked")
    public static Class<? extends Packet>[] idMapping = new Class[0];
    public static boolean[] priorityMapping = new boolean[0];
    //public final static Map<Class<? extends Packet>, Integer> classMapping = new HashMap<Class<? extends Packet>, Integer>();
    public final long timeStamp = System.currentTimeMillis();

    static {
	registerPacket(0, Packet0Disconnect.class);
	registerPacket(1, Packet1Login.class);
	registerPacket(2, Packet2Alert.class);
	registerPacket(3, Packet3Register.class);
	registerPacket(4, Packet4Sector.class);
	registerPacket(5, Packet5SectorRequest.class);
	registerPacket(6, Packet6BlankSector.class);
	registerPacket(7, Packet7EntityMove.class, true);
	registerPacket(8, Packet8EntityDispose.class);
	registerPacket(9, Packet9EntityData.class, true);
	registerPacket(10, Packet10EntityDataRequest.class);
	registerPacket(11, Packet11LocalEntityID.class);
	registerPacket(12, Packet12EntityDefRequest.class);
	registerPacket(13, Packet13EntityDef.class);
	registerPacket(14, Packet14ClientMove.class, true);
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
	}/* else if (classMapping.containsKey(pClass)) {
	    throw new IllegalArgumentException("Duplicate packet class: "
		    + pClass.getName());
	}*/ else {
	    if (id >= idMapping.length) {
		idMapping = Arrays.copyOf(idMapping.clone(),
			Math.max(id + 1, idMapping.length));
	    }
	    if (id >= priorityMapping.length) {
		priorityMapping = Arrays.copyOf(priorityMapping.clone(),
			Math.max(id + 1, idMapping.length));
	    }
	    idMapping[id] = pClass;
	    priorityMapping[id] = highPriority;
	    //classMapping.put(pClass, Integer.valueOf(id));
	}
    }

    public static Packet getPacket(int id) {
	try {
	    Class<? extends Packet> clazz = idMapping[id];
	    return clazz != null ? clazz.newInstance() : null;
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Skipping packet by id: " + id);
	    return null;
	}
    }

    public boolean isHighPriority() {
	return priorityMapping[getID()];
    }

    public String getName() {
	return getClass().getSimpleName();
    }

    protected static int stringByteLength(String str) {
	return 4 + (str.length() * 2);
    }

    public static Packet getPacket(PacketInputStream pIn) throws IOException {
	try {
	    int id = pIn.readInt();
	    Packet packet = getPacket(id);
	    if (packet == null)
		throw new IOException("Bad packet id: " + id);
	    packet.readData(pIn);
	    return packet;
	} catch (EOFException e) {
	    return null;
	}
    }

    public void writePacket(PacketOutputStream pOut) throws IOException {
	pOut.writeInt(getID());
	writeData(pOut);
    }

    public abstract int getID();

    protected abstract void writeData(PacketOutputStream pOut)
	    throws IOException;

    protected abstract void readData(PacketInputStream pIn) throws IOException;
}
