package com.pi.client;

public abstract class ClientThread extends Thread {
    protected boolean running = true;
    protected final Client client;

    public ClientThread(Client client) {
	super(client.getThreadGroup(), "ClientThread");
	super.setName(getClass().getSimpleName());
	this.client = client;
    }

    @Override
    public void run() {
	client.getLog().fine("Started: " + getClass().getSimpleName());
	while (running && shouldLoop()) {
	    loop();
	}
	client.getLog().fine("Stopped: " + getClass().getSimpleName());
    }
    
    private boolean shouldLoop(){
	return true;
    }

    protected abstract void loop();

    public void dispose() {
	running = false;
	try {
	    join();
	} catch (InterruptedException e) {
	    client.getLog().printStackTrace(e);
	    System.exit(0);
	}
    }
}
