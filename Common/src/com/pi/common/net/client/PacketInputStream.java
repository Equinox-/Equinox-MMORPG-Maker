package com.pi.common.net.client;

import java.io.*;

public class PacketInputStream extends DataInputStream {
    public PacketInputStream(InputStream in) {
	super(in);
    }

    public String readString() throws IOException {
	int strlen = readInt();
	if (strlen < 0)
	    throw new IOException("Invalid String Length: " + strlen);
	if (strlen == 0)
	    return "";
	char[] chars = new char[strlen];
	for (int i = 0; i < strlen; i++)
	    chars[i] = readChar();
	return new String(chars);
    }
    
    public byte[] readByteArray() throws IOException{
	byte[] read = new byte[readInt()];
	readFully(read);
	return read;
    }
}
