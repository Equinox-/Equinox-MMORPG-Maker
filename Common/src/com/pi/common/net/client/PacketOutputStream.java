package com.pi.common.net.client;

import java.io.*;

public class PacketOutputStream extends DataOutputStream {
    public PacketOutputStream(OutputStream out) {
	super(out);
    }

    public void writeString(String s) throws IOException {
	writeInt(s.length());
	char[] data = s.toCharArray();
	for (char c : data)
	    writeChar(c);
    }
    
    public void writeByteArray(byte[] data) throws IOException{
	writeInt(data.length);
	write(data);
    }
}
