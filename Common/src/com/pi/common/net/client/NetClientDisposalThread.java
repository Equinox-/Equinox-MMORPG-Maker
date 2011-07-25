package com.pi.common.net.client;

public class NetClientDisposalThread extends Thread {
    private final NetClient netClient;

    public NetClientDisposalThread(NetClient netClient) {
	this.netClient = netClient;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
	netClient.getLog().finer(
		"Disposing network client " + netClient.getID());
	if (netClient.getNetReader().isAlive())
	    try {
		netClient.getNetReader().join();
	    } catch (Exception e) {
		e.printStackTrace();
		netClient.getNetReader().stop();
	    }
	else
	    netClient.getLog().finer(
		    "Client " + netClient.getID()
			    + " reader thread is already stopped");
	if (netClient.getNetWriter().isAlive())
	    try {
		netClient.getNetWriter().join();
	    } catch (Exception e) {
		e.printStackTrace();
		netClient.getNetWriter().stop();
	    }

	else
	    netClient.getLog().finer(
		    "Client " + netClient.getID()
			    + " writer thread is already stopped");
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
	else
	    netClient.getLog().finer(
		    "Client " + netClient.getID()
			    + " processing thread is already stopped");
	netClient.getLog()
		.finer("Disposed network client " + netClient.getID());
    }
}
