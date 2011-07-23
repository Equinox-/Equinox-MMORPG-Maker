package com.pi.common.net.client;

public class NetClientProcessingThread extends Thread {
    private final NetClient netClient;

    public NetClientProcessingThread(final NetClient netClient) {
	super(netClient.getID() + " processing thread");
	this.netClient = netClient;
    }

    @Override
    public void run() {
	while (netClient.isConnected()
		&& (!netClient.isQuitting() || netClient.shouldProcessPacket())) {
	    netClient.processPacket();
	}
    }
}
