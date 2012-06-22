package com.pi.common.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class PacketInputStream {
	private ByteBuffer bb;

	public PacketInputStream(ByteBuffer bb) {
		this.bb = bb;
	}

	public void readBytes(byte[] data) {
		bb.get(data);
	}

	public byte readByte() {
		return bb.get();
	}

	public char readChar() {
		return bb.getChar();
	}

	public short readShort() {
		return bb.getShort();
	}

	public int readInt() {
		return bb.getInt();
	}

	public float readFloat() {
		return bb.getFloat();
	}

	public long readLong() {
		return bb.getLong();
	}

	public double readDouble() {
		return bb.getDouble();
	}

	public String readString() throws IOException {
		int strlen = readInt();
		if (strlen < 0)
			throw new IOException("Invalid String Length: " + strlen);
		if (strlen == 0)
			return "";
		char[] chars = new char[strlen];
		for (int i = 0; i < strlen; i++)
			chars[i] = readChar();
		return new String(chars);
	}

	public byte[] readByteArray() throws IOException {
		byte[] read = new byte[readInt()];
		readBytes(read);
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

	public ByteBuffer getByteBuffer() {
		return bb;
	}
}