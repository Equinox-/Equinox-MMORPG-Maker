package com.pi.client;

/**
 * This provides an easy to override class for creating threads bound to the
 * client.
 * 
 * @author Westin
 * 
 */
public abstract class ClientThread extends Thread {
	/**
	 * This boolean allows the client to shut down the thread.
	 * <p>
	 * The while loop contained inside the thread is bound to this variable and
	 * the {@link com.pi.client.ClientThread#shouldLoop()} method, allowing the
	 * thread to quit nicely.
	 */
	private boolean running = true;

	/**
	 * The client instance allowing subsystems access to each other.
	 */
	private final Client client;

	/**
	 * A mutex object for providing {@link java.lang.Object#wait()} and
	 * {@link java.lang.Object#notify()} access for the thread.
	 */
	private Object mutex;

	/**
	 * Creates a new thread in the client's thread group, with the class's
	 * simple name as the thread's name.
	 * 
	 * @param sClient The client instance
	 */
	public ClientThread(final Client sClient) {
		super(sClient.getThreadGroup(), "ClientThread");
		super.setName(getClass().getSimpleName());
		this.client = sClient;
	}

	/**
	 * Creates a mutex if one doesn't already exist.
	 * 
	 * @see com.pi.client.ClientThread#mutex
	 */
	protected final void createMutex() {
		if (mutex != null) {
			throw new IllegalStateException(
					"A mutex already exists!");
		}
		mutex = new Object();
	}

	/**
	 * Gets the client instance backing this thread.
	 * 
	 * @return the client instance
	 */
	protected final Client getClient() {
		return client;
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
		return running;
	}

	@Override
	public final void run() {
		client.getLog().fine(
				"Started: " + getClass().getSimpleName());
		while (running) {
			loop();
		}
		client.getLog().fine(
				"Stopped: " + getClass().getSimpleName());
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
			client.getLog().printStackTrace(e);
			System.exit(0);
		}
	}
}
