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
	while (running) {
	    loop();
	}
	client.getLog().fine("Stopped: " + getClass().getSimpleName());
    }

    protected abstract void loop();

    public void dispose() {
	running = false;
	try {
	    join();
	} catch (InterruptedException e) {
	    e.printStackTrace(client.getLog().getErrorStream());
	    System.exit(0);
	}
    }
}
