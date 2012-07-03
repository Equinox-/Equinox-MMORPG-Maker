package com.pi.server.net;

import com.pi.common.debug.PILogger;
import com.pi.common.net.DataWorker;

/**
 * Gets the data worker used by a network server.
 * 
 * @author Westin
 * 
 */
public class ServerDataWorker extends DataWorker {
	/**
	 * The network server.
	 */
	private final NetServer t;

	/**
	 * Creates a data worker for the given network server.
	 * 
	 * @param sT the network server
	 */
	public ServerDataWorker(final NetServer sT) {
		super(sT.getThreadGroup());
		this.t = sT;
	}

	@Override
	public final boolean isRunning() {
		return t.isConnected();
	}

	@Override
	public final PILogger getLog() {
		return t.getLog();
	}

}
