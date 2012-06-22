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

public class DatabaseIO {
	public static void write(OutputStream out, PacketObject obj)
			throws IOException {
		PacketOutputStream pO = new PacketOutputStream(ByteBuffer.allocate(obj
				.getLength()));
		obj.writeData(pO);
		out.write(pO.getByteBuffer().array());
	}

	public static void write(File f, PacketObject obj) throws IOException {
		if (!f.exists())
			f.createNewFile();
		FileOutputStream fO = new FileOutputStream(f);
		write(fO, obj);
		fO.close();
	}

	public static PacketObject read(InputStream in,
			Class<? extends PacketObject> clazz) throws IOException {
		try {

			PacketInputStream pIn = new PacketInputStream(readByteBuffer(in));
			PacketObject obj = clazz.newInstance();
			obj.readData(pIn);
			return obj;
		} catch (Exception e) {
			throw new IOException("The database object is corrupted!");
		}
	}

	public static PacketObject read(File f, Class<? extends PacketObject> clazz)
			throws IOException {
		return read(new FileInputStream(f), clazz);
	}

	public static ByteBuffer readByteBuffer(InputStream in) throws IOException {
		ByteBuffer bb = ByteBuffer.allocate(in.available());
		while (true) {
			int read = in.read();
			if (read == -1)
				break;
			if (!bb.hasRemaining()) {
				ByteBuffer n = ByteBuffer.allocate(bb.capacity()
						+ in.available());
				n.put(bb.array());
				bb = n;
			}
			bb.put((byte) read);
		}
		in.close();
		return (ByteBuffer) bb.flip();
	}
}
