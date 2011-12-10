package com.pi.server.net;

import com.pi.server.Server;
import com.pi.server.ServerThread;

public class NetServerProcessThread extends ServerThread {

    public NetServerProcessThread(Server server) {
	super(server);
    }

    @Override
    public void loop() {
	if (server.getClientManager() != null)
	    server.getClientManager().processPacketLoop();
	else
	    try {
		Thread.sleep(10l);
	    } catch (InterruptedException e) {
	    }
    }

    @Override
    public boolean shouldLoop() {
	return server.getNetwork() != null ? server.getNetwork().isConnected()
		: true;
    }
}
