package com.pi.common.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

public class PacketOutputStream {
	ByteBuffer bb;

	public PacketOutputStream(ByteBuffer bb) {
		this.bb = bb;
	}

	public void write(int b) {
		bb.put((byte) b);
	}

	public void write(byte[] d) {
		bb.put(d);
	}

	public void writeByte(int i) {
		bb.put((byte) i);
	}

	public void writeByte(byte i) {
		bb.put(i);
	}

	public void writeShort(short s) {
		bb.putShort(s);
	}

	public void writeShort(int dat) {
		bb.putShort((short) dat);
	}

	public void writeInt(int i) {
		bb.putInt(i);
	}

	public void writeLong(long l) {
		bb.putLong(l);
	}

	public void writeChar(char c) {
		bb.putChar(c);
	}

	public void writeFloat(float f) {
		bb.putFloat(f);
	}

	public void writeDouble(double d) {
		bb.putDouble(d);
	}

	public void writeString(String s) throws IOException {
		if (s == null) {
			writeInt(0);
		} else {
			writeInt(s.length());
			char[] data = s.toCharArray();
			for (char c : data)
				writeChar(c);
		}
	}

	public void writeByteArray(byte[] data) throws IOException {
		writeInt(data.length);
		write(data);
	}

	public void writeObject(Object o) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(bOut);
		objOut.writeObject(o);
		objOut.close();
		writeByteArray(bOut.toByteArray());
		bOut.close();
	}

	public static int stringByteLength(String str) {
		return 4 + (str != null ? str.length() * 2 : 0);
	}

	public ByteBuffer getByteBuffer() {
		return bb;
	}
}