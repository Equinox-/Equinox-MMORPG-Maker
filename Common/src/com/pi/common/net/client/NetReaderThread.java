package com.pi.common.net.client;

public class NetReaderThread extends Thread {
    private final NetClient netClient;

    public NetReaderThread(NetClient netClient) {
	super(netClient.getID() + " reader thread");
	this.netClient = netClient;
    }

    @Override
    public void run() {
	while (netClient.isConnected() && !netClient.isQuitting()) {
	    while (netClient.readPacket())
		;
	    try {
		sleep(100L);
	    } catch (InterruptedException e) {
	    }
	}
    }
}
