package com.pi.common.net;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * An output stream that outputs to a backing buffer.
 * 
 * @author Westin
 * 
 */
public class ByteBufferOutputStream extends OutputStream {
	/**
	 * The backing buffer.
	 */
	private ByteBuffer bb;

	/**
	 * Creates a byte buffer output stream with the given backing buffer.
	 * 
	 * @param sBb the backing buffer
	 */
	public ByteBufferOutputStream(final ByteBuffer sBb) {
		this.bb = sBb;
	}

	/**
	 * Creates a byte buffer output stream with the given size.
	 * 
	 * @param size the buffer size in bytes
	 */
	public ByteBufferOutputStream(final int size) {
		bb = ByteBuffer.allocate(size);
	}

	@Override
	public final void write(final int b) throws IOException {
		bb.put((byte) b);
	}

	/**
	 * Gets the backing buffer of this stream.
	 * 
	 * @return the byte buffer
	 */
	public final ByteBuffer getByteBuffer() {
		return bb;
	}
}
