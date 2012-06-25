package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * An object that is writable to a packet output stream, and readable from a
 * packet input stream.
 * 
 * @author Westin
 * 
 */
public interface PacketObject {
	/**
	 * Writes this object's data to the given output stream.
	 * 
	 * @param pOut the output stream
	 * @throws IOException if a write error occurs
	 */
	void writeData(PacketOutputStream pOut) throws IOException;

	/**
	 * Gets the byte length of the data written by this packet.
	 * 
	 * @return the byte length
	 */
	int getLength();

	/**
	 * Reads this packet's data from the given input stream.
	 * 
	 * @param pIn the input stream
	 * @throws IOException if a read error occurs
	 */
	void readData(PacketInputStream pIn) throws IOException;
}
