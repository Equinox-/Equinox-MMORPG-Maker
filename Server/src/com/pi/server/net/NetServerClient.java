package com.pi.server.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.pi.common.debug.PILogger;
import com.pi.common.net.NetChangeRequest;
import com.pi.common.net.NetClient;
import com.pi.common.net.NetHandler;
import com.pi.server.Server;
import com.pi.server.client.Client;

/**
 * A network client bound to a network server.
 * 
 * @author Westin
 * 
 */
public class NetServerClient extends NetClient {
	/**
	 * The server instance this client is bound to.
	 */
	private final Server server;
	/**
	 * The packet handler for this client.
	 */
	private NetHandler handler;
	/**
	 * This network client's identification number.
	 */
	private int clientID = -1;
	/**
	 * The client bound to this network instance.
	 */
	private Client cliRef = null;

	/**
	 * Creates a network server client for the given server and channel.
	 * 
	 * @param sServer the network server
	 * @param socket the socket channel
	 */
	public NetServerClient(final Server sServer,
			final SocketChannel socket) {
		super(socket);
		this.server = sServer;
		this.handler = new NetServerHandler(server, this);
	}

	/**
	 * Binds this network to the given client.
	 * 
	 * @param c the client to attach to this server
	 */
	public final void bindClient(final Client c) {
		if (cliRef == null) {
			cliRef = c;
		} else {
			throw new RuntimeException(
					"Client is already bound!");
		}
	}

	@Override
	public final PILogger getLog() {
		return server.getLog();
	}

	/**
	 * Binds this network to the given client id.
	 * 
	 * @param id the client id
	 */
	public final void bindToID(final int id) {
		this.clientID = id;
	}

	/**
	 * Get's this client's identification number.
	 * 
	 * @return the id number
	 */
	public final int getID() {
		return clientID;
	}

	/**
	 * Disposes this network client for the given reason.
	 * 
	 * @param reason the reason
	 * @param details the details behind the reason
	 */
	public final void dispose(final String reason,
			final String details) { // TODO
		server.getNetwork()
				.deregisterSocketChannel(getChannel());
	}

	/**
	 * Disposes this network client.
	 */
	public final void dispose() {
		server.getNetwork()
				.deregisterSocketChannel(getChannel());
	}

	@Override
	public final void processData(final byte[] data,
			final int off, final int len) {
		try {
			server.getNetwork().getWorker()
					.processData(this, data, off, len);
		} catch (IOException e) {
			getLog().printStackTrace(e);
		}
	}

	@Override
	public final void addWriteRequest() {
		server.getNetwork().addChangeRequest(
				new NetChangeRequest(getChannel(),
						NetChangeRequest.CHANGEOPS,
						SelectionKey.OP_WRITE));
	}

	@Override
	public final String getSuffix() {
		return " on " + clientID;
	}

	@Override
	public final void wakeSelector() {
		server.getNetwork().wakeSelector();
	}

	@Override
	public final NetHandler getHandler() {
		return handler;
	}

	@Override
	public final String toString() {
		return "NetClient[" + clientID + ","
				+ super.getHostAddress() + "]";
	}
}
