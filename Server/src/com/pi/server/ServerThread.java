package com.pi.server;

/**
 * This provides an easy to override class for creating threads bound to the
 * server.
 * 
 * @author Westin
 * 
 */
public abstract class ServerThread extends Thread {
	/**
	 * This boolean allows the server to shut down the thread.
	 * <p>
	 * The while loop contained inside the thread is bound to this variable and
	 * the {@link com.pi.server.ServerThread#shouldLoop()} method, allowing the
	 * thread to quit nicely.
	 */
	private boolean running = true;

	/**
	 * The server instance allowing subsystems access to each other.
	 */
	private final Server server;

	/**
	 * A mutex object for providing {@link java.lang.Object#wait()} and
	 * {@link java.lang.Object#notify()} access for the thread.
	 */
	private Object mutex;

	/**
	 * Creates a new thread in the server's thread group, with the class's
	 * simple name as the thread's name.
	 * 
	 * @param sServer The server instance
	 */
	public ServerThread(final Server sServer) {
		super(sServer.getThreadGroup(), "ServerThread");
		super.setName(getClass().getSimpleName());
		this.server = sServer;
	}

	/**
	 * Creates a mutex if one doesn't already exist.
	 * 
	 * @see com.pi.server.ServerThread#mutex
	 */
	protected final void createMutex() {
		if (mutex != null) {
			throw new IllegalStateException(
					"A mutex already exists!");
		}
		mutex = new Object();
	}

	/**
	 * Gets the server instance backing this thread.
	 * 
	 * @return the server instance
	 */
	protected final Server getServer() {
		return server;
	}

	/**
	 * Gets the current mutex, or null if one doesn't exist.
	 * 
	 * @return the mutex, or null if non-existent
	 */
	protected final Object getMutex() {
		return mutex;
	}

	/**
	 * Checks if the thread should be looping.
	 * <p>
	 * Note: A return value of <code>true</code> doesn't necessarily mean that
	 * the thread is running, just that it should be.
	 * 
	 * @return the loop state
	 */
	public final boolean isRunning() {
		return running && shouldLoop();
	}

	@Override
	public final void run() {
		server.getLog().fine(
				"Started: " + getClass().getSimpleName());
		while (running && shouldLoop()) {
			loop();
		}
		server.getLog().fine(
				"Stopped: " + getClass().getSimpleName());
	}

	/**
	 * This method, in addition to the
	 * {@link com.pi.server.ServerThread#running} variable determine if the
	 * thread should continue looping.
	 * 
	 * @return if the thread should continue
	 */
	protected boolean shouldLoop() {
		return true;
	}

	/**
	 * This method is called each loop of the thread.
	 */
	protected abstract void loop();

	/**
	 * Stops this thread.
	 */
	public final void dispose() {
		running = false;
		if (mutex != null) {
			synchronized (mutex) {
				mutex.notify();
			}
		}
		try {
			join();
		} catch (InterruptedException e) {
			server.getLog().printStackTrace(e);
			System.exit(0);
		}
	}
}
