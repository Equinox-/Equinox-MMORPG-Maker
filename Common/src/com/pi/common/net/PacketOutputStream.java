package com.pi.common.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import com.pi.common.constants.NetworkConstants.SizeOf;

/**
 * A wrapper around a byte buffer for writing to it in a way comparable to a
 * stream.
 * 
 * @author Westin
 * 
 */
public class PacketOutputStream {
	/**
	 * The byte buffer backing this output stream.
	 */
	private final ByteBuffer bb;

	/**
	 * Create a packet output stream with the specified backing byte buffer.
	 * 
	 * @param sBb the backing buffer
	 */
	public PacketOutputStream(final ByteBuffer sBb) {
		this.bb = sBb;
	}

	/**
	 * Writes the specified byte to the buffer.
	 * 
	 * @see ByteBuffer#put(byte)
	 * @param b the byte to write
	 */
	public final void write(final int b) {
		bb.put((byte) b);
	}

	/**
	 * Writes the specified bytes to the buffer.
	 * 
	 * @see ByteBuffer#put(byte[])
	 * @param d the bytes to write
	 */
	public final void write(final byte[] d) {
		bb.put(d);
	}

	/**
	 * Writes the specified byte to the buffer.
	 * 
	 * @see ByteBuffer#put(byte)
	 * @param i the byte to write
	 */
	public final void writeByte(final int i) {
		bb.put((byte) i);
	}

	/**
	 * Writes the specified byte to the buffer.
	 * 
	 * @see ByteBuffer#put(byte)
	 * @param i the byte to write
	 */
	public final void writeByte(final byte i) {
		bb.put(i);
	}

	/**
	 * Writes the specified short to the buffer as 2 bytes.
	 * 
	 * @see ByteBuffer#putShort(short)
	 * @param s the short to write
	 */
	public final void writeShort(final short s) {
		bb.putShort(s);
	}

	/**
	 * Writes the specified short to the buffer as 2 bytes.
	 * 
	 * @see ByteBuffer#putShort(short)
	 * @param dat the short to write
	 */
	public final void writeShort(final int dat) {
		bb.putShort((short) dat);
	}

	/**
	 * Writes the specified integer to the buffer as 4 bytes.
	 * 
	 * @see ByteBuffer#putInt(int)
	 * @param i the integer to write
	 */
	public final void writeInt(final int i) {
		bb.putInt(i);
	}

	/**
	 * Writes the specified long to the buffer as 8 bytes.
	 * 
	 * @see ByteBuffer#putLong(long)
	 * @param l the long to write
	 */
	public final void writeLong(final long l) {
		bb.putLong(l);
	}

	/**
	 * Writes the specified character to the buffer as 2 bytes.
	 * 
	 * @see ByteBuffer#putChar(char)
	 * @param c the character to write
	 */
	public final void writeChar(final char c) {
		bb.putChar(c);
	}

	/**
	 * Writes the specified float to the buffer as 4 bytes.
	 * 
	 * @see ByteBuffer#putFloat(float)
	 * @param f the float to write
	 */
	public final void writeFloat(final float f) {
		bb.putFloat(f);
	}

	/**
	 * Writes the specified double to the buffer as 8 bytes.
	 * 
	 * @see ByteBuffer#putDouble(double)
	 * @param d the double to write
	 */
	public final void writeDouble(final double d) {
		bb.putDouble(d);
	}

	/**
	 * Writes a string as an integer length and an array of characters to the
	 * buffer.
	 * 
	 * @param s the string to write.
	 */
	public final void writeString(final String s) {
		if (s == null) {
			writeInt(0);
		} else {
			writeInt(s.length());
			for (int i = 0; i < s.length(); i++) {
				writeChar(s.charAt(i));
			}
		}
	}

	/**
	 * Writes a byte array to the buffer as an integer length and an array of
	 * bytes.
	 * 
	 * @param data the array to write
	 */
	public final void writeByteArray(final byte[] data) {
		writeInt(data.length);
		write(data);
	}

	/**
	 * Writes a serialized object from the buffer using an ObjectInputStream.
	 * <p>
	 * This method first writes the object to a byte array using an
	 * {@link ObjectOutputStream} piped into a {@link ByteArrayOutputStream}.
	 * The byte array of the output stream is then written using the
	 * {@link PacketOutputStream#writeByteArray(byte[])} method.
	 * 
	 * @see ObjectOutputStream
	 * @param o the object read
	 * @throws IOException if there is a problem reading the object
	 */
	public final void writeObject(final Object o)
			throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ObjectOutputStream objOut = new ObjectOutputStream(bOut);
		objOut.writeObject(o);
		objOut.close();
		writeByteArray(bOut.toByteArray());
		bOut.close();
	}

	/**
	 * Gets the byte length of a given string.
	 * 
	 * @param str the string check
	 * @return the byte length of the string
	 */
	public static int stringByteLength(final String str) {
		if (str != null) {
			return SizeOf.INT + (SizeOf.CHAR * str.length());
		} else {
			return SizeOf.INT;
		}
	}

	/**
	 * Gets the buffer backing this packet output stream.
	 * 
	 * @return the backing buffer
	 */
	public final ByteBuffer getByteBuffer() {
		return bb;
	}
}