package com.pi.common.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class PacketOutputStream extends DataOutputStream {
	public PacketOutputStream(OutputStream out) {
		super(out);
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
}