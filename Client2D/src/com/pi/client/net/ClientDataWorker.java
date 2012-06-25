package com.pi.client.net;

import com.pi.common.debug.PILogger;
import com.pi.common.net.DataWorker;

/**
 * The data worker sub class for the client's network.
 * 
 * @author Westin
 * 
 */
public class ClientDataWorker extends DataWorker {
	/**
	 * The client network instance.
	 */
	private final ClientNetwork net;

	/**
	 * Creates and binds this data worker to the provided client network
	 * instance.
	 * 
	 * @param sNet the client network instance
	 */
	public ClientDataWorker(final ClientNetwork sNet) {
		super(sNet.getThreadGroup());
		this.net = sNet;
	}

	@Override
	public final boolean isRunning() {
		return net.isConnected();
	}

	@Override
	public final PILogger getLog() {
		return net.getLog();
	}
}
