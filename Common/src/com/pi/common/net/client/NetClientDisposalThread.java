package com.pi.common.net.client;

public class NetClientDisposalThread extends Thread {
    private final NetClient netClient;

    public NetClientDisposalThread(NetClient netClient) {
	super(netClient.getThreadGroup(), null, "NetClientDisposal-"
		+ netClient.getID());
	this.netClient = netClient;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
	if (netClient.getNetReader() != null
		&& netClient.getNetReader().isAlive())
	    try {
		netClient.getNetReader().join();
	    } catch (Exception e) {
		e.printStackTrace(netClient.getLog().getErrorStream());
		netClient.getNetReader().stop();
	    }
	if (netClient.getNetWriter() != null
		&& netClient.getNetWriter().isAlive())
	    try {
		netClient.getNetWriter().join();
	    } catch (Exception e) {
		e.printStackTrace(netClient.getLog().getErrorStream());
		netClient.getNetWriter().stop();
	    }
	while (netClient.shouldProcessPacket()) {
	    try {
		sleep(100l);
	    } catch (InterruptedException e) {
		e.printStackTrace(netClient.getLog().getErrorStream());
	    }
	}
	if (netClient.getNetProcessor() != null
		&& netClient.getNetProcessor().isAlive())
	    try {
		netClient.getNetProcessor().join();
	    } catch (Exception e) {
		e.printStackTrace(netClient.getLog().getErrorStream());
		netClient.getNetProcessor().stop();
	    }
	netClient.closeStreams();
    }
}
