package com.pi.client.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.pi.common.debug.PILogger;
import com.pi.common.net.NetChangeRequest;
import com.pi.common.net.NetClient;
import com.pi.common.net.NetHandler;

/**
 * The subclass of the {@link NetClient} class that provides a linkage to the
 * ClientNetwork class.
 * 
 * @see NetClient
 * @see ClientNetwork
 * @author Westin
 * 
 */
public class NetClientClient extends NetClient {
	/**
	 * The ClientNetwork instance this NetClient is bound to.
	 */
	private ClientNetwork network;

	/**
	 * Create a NetClient instance bound to the provided ClientNetwork instance,
	 * with the provided socket channel as network communication.
	 * 
	 * @param net the ClientNetwork instance
	 * @param socket the SocketChannel
	 */
	public NetClientClient(final ClientNetwork net,
			final SocketChannel socket) {
		super(socket);
		this.network = net;
	}

	@Override
	public final PILogger getLog() {
		return network.getLog();
	}

	@Override
	public final void processData(final byte[] data,
			final int off, final int len) {
		try {
			network.getWorker()
					.processData(this, data, off, len);
		} catch (IOException e) {
			getLog().printStackTrace(e);
		}
	}

	@Override
	public final void addWriteRequest() {
		network.addChangeRequest(new NetChangeRequest(
				getChannel(), NetChangeRequest.CHANGEOPS,
				SelectionKey.OP_WRITE));
	}

	@Override
	public final String getSuffix() {
		return "";
	}

	@Override
	public final void wakeSelector() {
		network.wakeSelector();
	}

	@Override
	public final NetHandler getHandler() {
		return network.getNetHandler();
	}

	@Override
	public final String toString() {
		return "NetClient[" + super.getHostAddress() + "]";
	}
}
