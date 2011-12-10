package com.pi.common.net.client;

public class NetClientDisposalThread extends Thread {
    private final NetClient netClient;

    public NetClientDisposalThread(NetClient netClient) {
	super(netClient.getThreadGroup(), null, "NetClientDisposal-"
		+ netClient.getID());
	this.netClient = netClient;
    }

    @Override
    public void run() {
	netClient.forceDispose();
    }
}
