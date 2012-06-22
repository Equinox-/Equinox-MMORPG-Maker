package com.pi.client.net;

import com.pi.common.debug.PILogger;
import com.pi.common.net.DataWorker;

public class ClientDataWorker extends DataWorker {
	private final ClientNetwork t;

	public ClientDataWorker(ClientNetwork t) {
		super(t.getThreadGroup());
		this.t = t;
	}

	@Override
	public boolean isRunning() {
		return t.isConnected();
	}

	@Override
	public PILogger getLog() {
		return t.getLog();
	}
}
