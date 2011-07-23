package com.pi.common.net.client;

public class NetClientDisposalThread extends Thread {
    private final NetClient netClient;

    public NetClientDisposalThread(NetClient netClient) {
	this.netClient = netClient;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
	if (netClient.getNetReader().isAlive())
	    try {
		netClient.getNetReader().join();
	    } catch (Exception e) {
		e.printStackTrace();
		netClient.getNetReader().stop();
	    }
	if (netClient.getNetWriter().isAlive())
	    try {
		netClient.getNetWriter().join();
	    } catch (Exception e) {
		e.printStackTrace();
		netClient.getNetWriter().stop();
	    }
	while (netClient.shouldProcessPacket()) {
	    try {
		sleep(100l);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
	if (netClient.getNetProcessor().isAlive())
	    try {
		netClient.getNetProcessor().join();
	    } catch (Exception e) {
		e.printStackTrace();
		netClient.getNetProcessor().stop();
	    }
    }
}
