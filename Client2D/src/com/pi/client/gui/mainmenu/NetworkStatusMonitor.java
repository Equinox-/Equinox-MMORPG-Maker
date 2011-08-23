package com.pi.client.gui.mainmenu;

import com.pi.client.Client;

public class NetworkStatusMonitor extends Thread {
    private static final long pollTime = 15000l;
    private boolean isOnline = false;
    private final Client client;

    public NetworkStatusMonitor(Client client) {
	super("Net-Status-Monitor");
	this.client = client;
	start();
    }

    public boolean isOnline() {
	return isOnline;
    }

    @Override
    public void run() {
	long lastPoll = 0;
	while (!client.isInGame()) {
	    if (System.currentTimeMillis() - lastPoll > pollTime) {
		client.getNetwork().connect(client.getNetworkIP(),
			client.getNetworkPort());
	    } else {
		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
	    }
	}
    }
}
