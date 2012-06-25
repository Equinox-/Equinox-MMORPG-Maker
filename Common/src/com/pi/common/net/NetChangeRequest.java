package com.pi.common.net;

import java.nio.channels.SocketChannel;

/**
 * A class representing a change request on a selector thread.
 * 
 * @author Westin
 * 
 */
public class NetChangeRequest {
	/**
	 * The register type.
	 */
	public static final int REGISTER = 1;
	/**
	 * The change operation type.
	 */
	public static final int CHANGEOPS = 2;

	/**
	 * The socket channel to do the change on.
	 */
	private final SocketChannel socket;
	/**
	 * The type of this change request.
	 * 
	 * @see NetChangeRequest#REGISTER
	 * @see NetChangeRequest#CHANGEOPS
	 */
	private final int type;
	/**
	 * The operations to perform.
	 */
	private final int ops;

	/**
	 * Creates a net change request with the given socket, type, and operations.
	 * 
	 * @param sSocket the socket channel to do the modifications on
	 * @param sType the type of the net change request, either
	 *            {@link NetChangeRequest#REGISTER}, or
	 *            {@link NetChangeRequest#CHANGEOPS}
	 * @param sOps the operations to perfom
	 */
	public NetChangeRequest(final SocketChannel sSocket,
			final int sType, final int sOps) {
		this.socket = sSocket;
		this.type = sType;
		this.ops = sOps;
	}

	/**
	 * The channel to perform the operations on.
	 * 
	 * @return the socket channel
	 */
	public final SocketChannel getChannel() {
		return socket;
	}

	/**
	 * Gets the operations to perform.
	 * 
	 * @return the operations
	 */
	public final int getOperations() {
		return ops;
	}

	/**
	 * The type of this change request.
	 * 
	 * @see NetChangeRequest#REGISTER
	 * @see NetChangeRequest#CHANGEOPS
	 * @return the type
	 */
	public final int getType() {
		return type;
	}
}
