package com.pi.common.net.client;

public class NetReaderThread extends Thread {
    private final NetClient netClient;

    public NetReaderThread(NetClient netClient) {
	super(netClient.getThreadGroup(), null,netClient.getID() + " reader thread");
	this.netClient = netClient;
    }

    @Override
    public void run() {
	netClient.getLog().finer(
		"Starting client " + netClient.getID() + " reader thread");
	while (netClient.isConnected() && !netClient.isQuitting()
		&& !netClient.getSocket().isInputShutdown()) {
	    while (netClient.readPacket())
		;
	    try {
		sleep(100L);
	    } catch (InterruptedException e) {
	    }
	}
	netClient.getLog().finer(
		"Quit client " + netClient.getID() + " reader thread");
    }
}
