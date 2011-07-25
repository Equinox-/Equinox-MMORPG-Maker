package com.pi.common.net.client;

public class NetClientProcessingThread extends Thread {
    private final NetClient netClient;

    public NetClientProcessingThread(final NetClient netClient) {
	super(netClient.getID() + " processing thread");
	this.netClient = netClient;
    }

    @Override
    public void run() {
	netClient.getLog().finer(
		"Starting client " + netClient.getID() + " processing thread");
	while (netClient.isConnected()
		&& (!netClient.isQuitting() || netClient.shouldProcessPacket())) {
	    netClient.processPacket();
	}
	netClient.getLog().finer(
		"Quit client " + netClient.getID() + " processing thread");
    }
}
