package com.pi.common.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

import com.pi.common.constants.NetworkConstants.SizeOf;

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
	 * More specifically this method reads array length bytes from the buffer
	 * into the array, padding the array with 0s if there aren't enough bytes
	 * remaining.
	 * 
	 * @see ByteBuffer#get(byte[])t
	 * @param data the array to read in
	 */
	public final void readBytes(final byte[] data) {
		if (bb.remaining() >= data.length) {
			bb.get(data);
		} else {
			int count = bb.remaining();
			bb.get(data, 0, count);
			for (int i = count; i < data.length; i++) {
				data[i] = 0;
			}
			close();
		}
	}

	/**
	 * Reads a byte from the buffer.
	 * 
	 * More specifically this method reads 1 byte from the buffer if it has at
	 * least 1 byte remaining, otherwise, this method returns 0.
	 * 
	 * @see ByteBuffer#get()
	 * @return the byte read
	 */
	public final byte readByte() {
		if (bb.remaining() >= SizeOf.BYTE) {
			return bb.get();
		} else {
			close();
			return 0;
		}
	}

	/**
	 * Reads a char from the buffer.
	 * 
	 * More specifically this method reads 1 byte from the buffer if it has at
	 * least 1 byte remaining, otherwise, this method returns 0.
	 * 
	 * @see ByteBuffer#getChar()
	 * @return the char read
	 */
	public final char readChar() {
		if (bb.remaining() >= SizeOf.CHAR) {
			return bb.getChar();
		} else {
			close();
			return 0;
		}
	}

	/**
	 * Reads a 2 byte short from the buffer.
	 * 
	 * More specifically this method reads 2 bytes from the buffer if it has at
	 * least 2 bytes remaining, otherwise, this method returns 0.
	 * 
	 * @see ByteBuffer#getShort()
	 * @return the short read
	 */
	public final short readShort() {
		if (bb.remaining() >= SizeOf.SHORT) {
			return bb.getShort();
		} else {
			close();
			return 0;
		}
	}

	/**
	 * Reads a 4 byte integer from the buffer.
	 * 
	 * More specifically this method reads 4 bytes from the buffer if it has at
	 * least 4 bytes remaining, otherwise, this method returns 0.
	 * 
	 * @see ByteBuffer#getInt()
	 * @return the integer read
	 */
	public final int readInt() {
		if (bb.remaining() >= SizeOf.INT) {
			return bb.getInt();
		} else {
			close();
			return 0;
		}
	}

	/**
	 * Reads a 4 byte float from the buffer.
	 * 
	 * More specifically this method reads 4 bytes from the buffer if it has at
	 * least 4 bytes remaining, otherwise, this method returns 0.
	 * 
	 * @see ByteBuffer#getFloat()
	 * @return the float read
	 */
	public final float readFloat() {
		if (bb.remaining() >= SizeOf.FLOAT) {
			return bb.getFloat();
		} else {
			close();
			return 0;
		}
	}

	/**
	 * Reads a 8 byte long from the buffer.
	 * 
	 * More specifically this method reads 8 bytes from the buffer if it has at
	 * least 8 bytes remaining, otherwise, this method returns 0.
	 * 
	 * @see ByteBuffer#getLong()
	 * @return the long read
	 */
	public final long readLong() {
		if (bb.remaining() >= SizeOf.LONG) {
			return bb.getLong();
		} else {
			close();
			return 0;
		}
	}

	/**
	 * Reads a 8 byte double from the buffer.
	 * 
	 * More specifically this method reads 8 bytes from the buffer if it has at
	 * least 8 bytes remaining, otherwise, this method returns 0.
	 * 
	 * @see ByteBuffer#getDouble()
	 * @return the double read
	 */
	public final double readDouble() {
		if (bb.remaining() >= SizeOf.DOUBLE) {
			return bb.getDouble();
		} else {
			close();
			return 0;
		}
	}

	/**
	 * Reads a string as an integer length and an array of characters from the
	 * buffer.
	 * 
	 * This method will also pad the string with characters with the code of 0
	 * if the buffer doesn't have enough remaining elements in the buffer.
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
	 * This method will also pad the byte array with 0s if there isn't enough
	 * remaining elements in the buffer.
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

	/**
	 * Gets the number of remaining bytes in this byte buffer stream.
	 * 
	 * @return the number of remaining bytes
	 */
	public final int available() {
		return bb.remaining();
	}

	/**
	 * Closes this buffer by making the remaining bytes readable by this buffer
	 * 0.
	 */
	public final void close() {
		bb.position(bb.capacity());
	}
}