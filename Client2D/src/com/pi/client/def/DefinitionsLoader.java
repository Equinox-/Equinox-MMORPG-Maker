package com.pi.client.def;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi.client.Client;
import com.pi.common.game.ObjectHeap;

/**
 * A utility class for loading abstract definition types.
 * 
 * @author Westin
 * 
 * @param <E> The definitions class to be loaded.
 */
public abstract class DefinitionsLoader<E> {
	/**
	 * How long for a request to the server to be re-made in milliseconds.
	 */
	private static final long REQUEST_EXPIRY = 5000;

	/**
	 * The client instance.
	 */
	private final Client client;
	/**
	 * The object heap storing the definitions.
	 */
	private ObjectHeap<DefStorage<E>> map =
			new ObjectHeap<DefStorage<E>>();
	/**
	 * The load queue for adding definitions to the loading process.
	 */
	private Queue<Integer> loadQueue =
			new LinkedBlockingQueue<Integer>();
	/**
	 * The request queue for monitoring the state of requests to the server.
	 */
	private ObjectHeap<Long> requestQueue =
			new ObjectHeap<Long>();

	/**
	 * Creates a definition loader with the specified client as the controller.
	 * 
	 * @param sClient the controller
	 */
	public DefinitionsLoader(final Client sClient) {
		this.client = sClient;
	}

	/**
	 * Gets the definition storage for a specific definition ID. If it is not
	 * loaded, adds it to the queue.
	 * 
	 * @param defID the definition ID
	 * @return the definition storage instance, or <code>null</code> if not
	 *         loaded.
	 */
	private DefStorage<E> getStorage(final int defID) {
		Integer p = new Integer(defID);
		DefStorage<E> sS = map.get(p);
		if ((sS == null || (sS.def == null && !sS.empty))
				&& (requestQueue.get(defID) == null || requestQueue
						.get(defID) + REQUEST_EXPIRY < System
							.currentTimeMillis())) {
			loadQueue.add(defID);
			client.getDefs().notifyMutex();
			return null;
		}
		return sS;
	}

	/**
	 * Checks if a definition is not loaded, or is an empty definition.
	 * 
	 * @param defID the definition id
	 * @return if the definition isn't loaded or is empty
	 * @see DefinitionsLoader#getStorage(int)
	 */
	public final boolean isEmpty(final int defID) {
		DefStorage<E> stor = getStorage(defID);
		if (stor != null) {
			return stor.empty;
		} else {
			return true;
		}
	}

	/**
	 * Gets the definition instance and returns it.
	 * 
	 * @param defID the definition id
	 * @return the definitions instance, or <code>null</code> if empty or not
	 *         loaded.
	 * @see DefinitionsLoader#getStorage(int)
	 */
	public final E getDef(final int defID) {
		DefStorage<E> stor = getStorage(defID);
		if (stor != null) {
			return stor.def;
		} else {
			return null;
		}
	}

	/**
	 * Assigns a definition instance to a definition ID. Should be invoked by
	 * the handling method for data packet received from the server.
	 * 
	 * @param defID the definition id
	 * @param eDef the definition instance
	 */
	public final void setDef(final int defID, final E eDef) {
		DefStorage<E> str = map.get(defID);
		if (str == null) {
			str = new DefStorage<E>();
		}
		str.def = eDef;
		str.empty = eDef == null;
		map.set(defID, str);
		requestQueue.remove(defID);
	}

	/**
	 * The load processing loop. This should be called from the same thread as
	 * the rest of the definitions.
	 */
	public final void loadLoop() {
		if (loadQueue.size() > 0) {
			int defID = loadQueue.poll();
			if (defID != -1
					&& map.get(defID) == null
					&& (requestQueue.get(defID) == null || requestQueue
							.get(defID) + REQUEST_EXPIRY < System
								.currentTimeMillis())) {
				if (requestDefinition(defID)) {
					requestQueue.set(defID,
							System.currentTimeMillis());
				}
			}
		}
	}

	/**
	 * Sends the definition request packet to the server, and returns
	 * <code>true</code> if successfully sent.
	 * 
	 * @param defID the definition id to request
	 * @return if the request was made
	 */
	protected abstract boolean requestDefinition(int defID);

	/**
	 * Class representing the storage of definitions in the storage heap.
	 * 
	 * @author Westin
	 * 
	 * @param <E> the definition class
	 */
	private static class DefStorage<E> {
		/**
		 * Flag representing the empty, but loaded state of a definition.
		 */
		private boolean empty = false;
		/**
		 * The definition instance.
		 */
		private E def;
	}

	/**
	 * Obtains a heap representing all the loaded definitions.
	 * 
	 * @return the loaded heap
	 */
	public final ObjectHeap<DefStorage<E>> loadedMap() {
		return map;
	}

	/**
	 * Gets the client instance bound to this loader.
	 * 
	 * @return the client instance
	 */
	protected final Client getClient() {
		return client;
	}
}
