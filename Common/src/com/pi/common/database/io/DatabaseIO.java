package com.pi.common.database.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * Utility class to read and write packet objects.
 * 
 * @author Westin
 * 
 */
public final class DatabaseIO {
	/**
	 * Writes a packet object provided to the given output stream.
	 * 
	 * @param out the stream to write to
	 * @param obj the object to write
	 * @throws IOException if there was a write issue
	 */
	public static void write(final OutputStream out,
			final PacketObject obj) throws IOException {
		PacketOutputStream pO =
				new PacketOutputStream(ByteBuffer.allocate(obj
						.getLength()));
		obj.writeData(pO);
		out.write(pO.getByteBuffer().array());
		out.close();
	}

	/**
	 * Writes a packet object provided to the given file.
	 * 
	 * @param f the file to write to
	 * @param obj the object to write
	 * @throws IOException if there was a write issue
	 */
	public static void write(final File f, final PacketObject obj)
			throws IOException {
		if (!f.exists()) {
			f.createNewFile();
		}
		FileOutputStream fO = new FileOutputStream(f);
		write(fO, obj);
	}

	/**
	 * Reads a packet object by the type defined by the provided class, from the
	 * provided input stream.
	 * 
	 * @param in the stream to read from
	 * @param clazz the class to read into
	 * @return the read object
	 * @throws IOException if there is a problem reading the stream, or with the
	 *             contents of the stream
	 */
	public static PacketObject read(final InputStream in,
			final Class<? extends PacketObject> clazz)
			throws IOException {
		try {

			PacketInputStream pIn =
					new PacketInputStream(readByteBuffer(in));
			PacketObject obj = clazz.newInstance();
			obj.readData(pIn);
			return obj;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(
					"The database object is corrupted or not of this type!");
		}
	}

	/**
	 * Reads a packet object by the type defined by the provided class, from the
	 * provided file.
	 * 
	 * @see DatabaseIO#read(InputStream, Class)
	 * @param f the file to read from
	 * @param clazz the class to read into
	 * @return the read object
	 * @throws IOException if there is a problem reading the stream, or with the
	 *             contents of the stream
	 */
	public static PacketObject read(final File f,
			final Class<? extends PacketObject> clazz)
			throws IOException {
		return read(new FileInputStream(f), clazz);
	}

	/**
	 * Read the contents of an input stream into a byte buffer.
	 * 
	 * @param in the stream to read from
	 * @return the byte buffer read into
	 * @throws IOException if there is a error reading from the stream
	 */
	public static ByteBuffer readByteBuffer(final InputStream in)
			throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(in.available());
		while (true) {
			int read = in.read();
			if (read == -1) {
				break;
			}
			if (!bb.hasRemaining()) {
				ByteBuffer n =
						ByteBuffer.allocate(bb.capacity()
								+ in.available());
				n.put(bb.array());
				bb = n;
			}
			bb.put((byte) read);
		}
		in.close();
		return (ByteBuffer) bb.flip();
	}

	/**
	 * Overridden constructor to disallow the creation of instances.
	 */
	private DatabaseIO() {
	}
}
