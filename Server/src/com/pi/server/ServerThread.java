package com.pi.server;


public abstract class ServerThread extends Thread {
    protected boolean running = true;
    protected final Server server;

    public ServerThread(Server server) {
	super(server.getThreadGroup(), "ServerThread");
	super.setName(getClass().getSimpleName());
	this.server = server;
    }

    @Override
    public void run() {
	server.getLog().fine("Started: " + getClass().getSimpleName());
	while (running && shouldLoop()) {
	    loop();
	}
	server.getLog().fine("Stopped: " + getClass().getSimpleName());
    }

    public abstract void loop();
    
    public boolean shouldLoop(){
	return true;
    }

    public void dispose() {
	running = false;
	try {
	    join();
	} catch (InterruptedException e) {
	    e.printStackTrace(server.getLog().getErrorStream());
	    System.exit(0);
	}
    }
}
