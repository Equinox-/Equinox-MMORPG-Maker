package com.pi.common.net.packet;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public abstract class Packet {
    public final static Map<Integer, Class<? extends Packet>> idMapping = new HashMap<Integer, Class<? extends Packet>>();
    public final static Map<Class<? extends Packet>, Integer> classMapping = new HashMap<Class<? extends Packet>, Integer>();
    public final long timeStamp = System.currentTimeMillis();

    static {
	registerPacket(0, Packet0Disconnect.class);
	registerPacket(1, Packet1Login.class);
	registerPacket(2, Packet2Alert.class);
	registerPacket(3, Packet3Register.class);
	registerPacket(4, Packet4Sector.class);
	registerPacket(5, Packet5SectorRequest.class);
    }

    public Packet() {
    }

    public static void registerPacket(int id, Class<? extends Packet> pClass) {
	if (idMapping.containsKey(Integer.valueOf(id))) {
	    throw new IllegalArgumentException("Duplicate packet id: " + id);
	} else if (classMapping.containsKey(pClass)) {
	    throw new IllegalArgumentException("Duplicate packet class: "
		    + pClass.getName());
	} else {
	    idMapping.put(Integer.valueOf(id), pClass);
	    classMapping.put(pClass, Integer.valueOf(id));
	}
    }

    public static Packet getPacket(int id) {
	try {
	    Class<? extends Packet> clazz = idMapping.get(Integer.valueOf(id));
	    return clazz != null ? clazz.newInstance() : null;
	} catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("Skipping packet by id: " + id);
	    return null;
	}
    }

    protected static String readString(DataInputStream dIn) throws IOException {
	int strlen = dIn.readInt();
	if (strlen < 0)
	    throw new IOException("Invalid String Length: " + strlen);
	if (strlen == 0)
	    return "";
	char[] chars = new char[strlen];
	for (int i = 0; i < strlen; i++)
	    chars[i] = dIn.readChar();
	return new String(chars);
    }

    protected static void writeString(DataOutputStream dOut, String s)
	    throws IOException {
	dOut.writeInt(s.length());
	char[] data = s.toCharArray();
	for (char c : data)
	    dOut.writeChar(c);
    }

    public String getName() {
	return idMapping.get(getID()).getSimpleName();
    }

    protected static int stringByteLength(String str) {
	return 4 + (str.length() * 2);
    }

    public static Packet getPacket(DataInputStream dIn) throws IOException {
	try {
	    int id = dIn.readInt();
	    Packet packet = getPacket(id);
	    if (packet == null)
		throw new IOException("Bad packet id: " + id);
	    packet.readData(dIn);
	    return packet;
	} catch (EOFException e) {
	    return null;
	}
    }

    public void writePacket(DataOutputStream dOut) throws IOException {
	dOut.writeInt(getID());
	writeData(dOut);
    }

    public int getID() {
	Integer id = classMapping.get(getClass());
	if (id != null)
	    return id.intValue();
	else
	    throw new NullPointerException("This packet is not registered!");
    }

    protected abstract void writeData(DataOutputStream dOut) throws IOException;

    protected abstract void readData(DataInputStream dIn) throws IOException;
}
