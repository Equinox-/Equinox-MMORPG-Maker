package com.pi.common.net.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

public class PacketInputStream {
    private final ByteBuffer in;

    public PacketInputStream(ByteBuffer in) {
	this.in = in;
    }

    public String readString() throws IOException {
	int strlen = in.getInt();
	if (strlen < 0)
	    throw new IOException("Invalid String Length: " + strlen);
	if (strlen == 0)
	    return "";
	char[] chars = new char[strlen];
	for (int i = 0; i < strlen; i++)
	    chars[i] = in.getChar();
	return new String(chars);
    }

    public byte[] readByteArray() throws IOException {
	byte[] read = new byte[in.getInt()];
	in.get(read);
	return read;
    }

    public Object readObject() throws IOException, ClassNotFoundException {
	ByteArrayInputStream bIn = new ByteArrayInputStream(readByteArray());
	ObjectInputStream objIn = new ObjectInputStream(bIn);
	Object o = objIn.readObject();
	objIn.close();
	bIn.close();
	return o;
    }

    public int readInt() {
	return in.getInt();
    }

    public long readLong() {
	return in.getLong();
    }

    public short readShort() {
	return in.getShort();
    }

    public byte read() {
	return in.get();
    }

    public float readFloat() {
	return in.getFloat();
    }

    public double readDouble() {
	return in.getDouble();
    }
}
