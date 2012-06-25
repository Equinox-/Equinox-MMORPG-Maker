package com.pi.common.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

/**
 * A wrapper around a byte buffer for reading from it in a way comparable to a
 * stream.
 * 
 * @author Westin
 * 
 */
public class PacketInputStream {
	/**
	 * The byte buffer backing this input stream.
	 */
	private final ByteBuffer bb;

	/**
	 * Creates a packet input stream with the specified buffer backing it.
	 * 
	 * @param sBb the backing buffer
	 */
	public PacketInputStream(final ByteBuffer sBb) {
		this.bb = sBb;
	}

	/**
	 * Reads an array of bytes from the buffer.
	 * 
	 * @see ByteBuffer#get(byte[])t
	 * @param data the array to read in
	 */
	public final void readBytes(final byte[] data) {
		bb.get(data);
	}

	/**
	 * Reads a byte from the buffer.
	 * 
	 * @see ByteBuffer#get()
	 * @return the byte read
	 */
	public final byte readByte() {
		return bb.get();
	}

	/**
	 * Reads a char from the buffer.
	 * 
	 * @see ByteBuffer#getChar()
	 * @return the char read
	 */
	public final char readChar() {
		return bb.getChar();
	}

	/**
	 * Reads a 2 byte short from the buffer.
	 * 
	 * @see ByteBuffer#getShort()
	 * @return the short read
	 */
	public final short readShort() {
		return bb.getShort();
	}

	/**
	 * Reads a 4 byte integer from the buffer.
	 * 
	 * @see ByteBuffer#getInt()
	 * @return the integer read
	 */
	public final int readInt() {
		return bb.getInt();
	}

	/**
	 * Reads a 4 byte float from the buffer.
	 * 
	 * @see ByteBuffer#getFloat()
	 * @return the float read
	 */
	public final float readFloat() {
		return bb.getFloat();
	}

	/**
	 * Reads a 8 byte long from the buffer.
	 * 
	 * @see ByteBuffer#getLong()
	 * @return the long read
	 */
	public final long readLong() {
		return bb.getLong();
	}

	/**
	 * Reads a 8 byte double from the buffer.
	 * 
	 * @see ByteBuffer#getDouble()
	 * @return the double read
	 */
	public final double readDouble() {
		return bb.getDouble();
	}

	/**
	 * Reads a string as an integer length and an array of characters from the
	 * buffer.
	 * 
	 * @return the string read
	 * @throws IOException if the string length was invalid
	 */
	public final String readString() throws IOException {
		int strlen = readInt();
		if (strlen < 0) {
			throw new IOException("Invalid String length: "
					+ strlen);
		}
		if (strlen == 0) {
			return "";
		}
		char[] chars = new char[strlen];
		for (int i = 0; i < strlen; i++) {
			chars[i] = readChar();
		}
		return new String(chars);
	}

	/**
	 * Reads a byte array from the buffer as an integer length and an array of
	 * bytes.
	 * 
	 * @return the read byte array
	 * @throws IOException if the array length was invalid
	 */
	public final byte[] readByteArray() throws IOException {
		int length = readInt();
		if (length < 0) {
			throw new IOException("Invalid array length: "
					+ length);
		}
		byte[] read = new byte[length];
		readBytes(read);
		return read;
	}

	/**
	 * Reads a serialized object from the buffer using an ObjectInputStream.
	 * <p>
	 * This method first reads a byte array using the
	 * {@link PacketInputStream#readByteArray()} method, then pipes this through
	 * a {@link ByteArrayInputStream} to an {@link ObjectInputStream}.
	 * 
	 * @see ObjectInputStream
	 * @return the object read
	 * @throws IOException if there is a problem reading the object
	 * @throws ClassNotFoundException if the object's class is unknown
	 */
	public final Object readObject() throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bIn =
				new ByteArrayInputStream(readByteArray());
		ObjectInputStream objIn = new ObjectInputStream(bIn);
		Object o = objIn.readObject();
		objIn.close();
		bIn.close();
		return o;
	}

	/**
	 * Gets the byte buffer backing this stream.
	 * 
	 * @return the byte buffer
	 */
	public final ByteBuffer getByteBuffer() {
		return bb;
	}
}