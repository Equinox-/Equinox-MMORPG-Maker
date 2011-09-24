package com.pi.common.net.client;

public class NetClientSpeedMonitor extends Thread {
    // Speed
    private int cacheUploadRate = -1;
    private int cacheDownloadRate = -1;
    private long lastUpdateTime = -1;
    private int sendSinceUpdate = 0;
    private int recieveSinceUpdate = 0;
    private final NetClient netClient;
    private Object sync = new Object();

    public NetClientSpeedMonitor(final NetClient nC) {
	super("Speed Monitor " + nC.getID());
	this.netClient = nC;
	start();
    }

    @Override
    public void run() {
	netClient.getLog().finer(
		"Starting client " + netClient.getID()
			+ " speed monitor thread");
	while (netClient.isConnected() && !netClient.isQuitting()) {
	    synchronized (sync) {
		long delta = lastUpdateTime >= 0 ? System.currentTimeMillis()
			- lastUpdateTime : 1000;
		if (delta >= 1000) {
		    cacheUploadRate = (int) ((sendSinceUpdate * 1000) / delta);
		    cacheDownloadRate = (int) ((recieveSinceUpdate * 1000) / delta);
		    lastUpdateTime = System.currentTimeMillis();
		    sendSinceUpdate = 0;
		    recieveSinceUpdate = 0;
		}
	    }
	}
	netClient.getLog().finer(
		"Quit client " + netClient.getID() + " speed monitor thread");
    }

    public void addSent(int count) {
	synchronized (sync) {
	    sendSinceUpdate += count;
	}
    }

    public void addRecieve(int count) {
	synchronized (sync) {
	    recieveSinceUpdate += count;
	}
    }
    
    public int getUploadSpeed(){
	return cacheUploadRate;
    }
    public int getDownloadSpeed(){
	return cacheDownloadRate;
    }
}
