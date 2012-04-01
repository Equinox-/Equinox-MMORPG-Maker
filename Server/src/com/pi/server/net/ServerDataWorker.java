package com.pi.server.net;

import com.pi.common.debug.PILogger;
import com.pi.common.net.DataWorker;

public class ServerDataWorker extends DataWorker {

    private final NetServer t;

    public ServerDataWorker(NetServer t) {
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
