package com.pi.common.net.client;

import java.io.IOException;

public class NetWriterThread extends Thread {
    private final NetClient netClient;

    public NetWriterThread(NetClient netClient) {
	super(netClient.getID() + " reader thread");
	this.netClient = netClient;
    }

    @Override
    public void run() {
	netClient.getLog().finer(
		"Starting client " + netClient.getID() + " writer thread");
	while (netClient.isConnected() && !netClient.isQuitting()) {
	    while (netClient.sendQueuedPacket())
		;
	    try {
		sleep(100L);
	    } catch (InterruptedException e) {
	    }
	    try {
		netClient.flushOutput();
	    } catch (IOException e) {
		if (!netClient.hasErrored())
		    netClient.error(e);
		e.printStackTrace();
	    }
	}
	netClient.getLog().finer(
		"Quit client " + netClient.getID() + " writer thread");
    }
}
