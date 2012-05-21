package com.pi.common.net;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {
	private ByteBuffer bb;

	public ByteBufferOutputStream(int size) {
		bb = ByteBuffer.allocate(size);
	}

	@Override
	public void write(int b) throws IOException {
		bb.put((byte) b);
	}

	public ByteBuffer getByteBuffer() {
		return bb;
	}
}
