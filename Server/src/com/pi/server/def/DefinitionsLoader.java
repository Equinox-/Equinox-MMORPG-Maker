package com.pi.server.def;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi.common.game.ObjectHeap;
import com.pi.server.Server;

/**
 * A utility class for loading abstract definition types.
 * 
 * @author Westin
 * 
 * @param <E> The definitions class to be loaded.
 */
public abstract class DefinitionsLoader<E> {

	/**
	 * The server instance.
	 */
	private final Server server;
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
	 * Creates a definition loader with the specified server as the controller.
	 * 
	 * @param sServer the controller
	 */
	public DefinitionsLoader(final Server sServer) {
		this.server = sServer;
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
		if (sS == null || (sS.def == null && !sS.empty)) {
			loadQueue.add(defID);
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
	 * the handling method for data packet recieved from the server.
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
	}

	/**
	 * The load processing loop. This should be called from the same thread as
	 * the rest of the definitions.
	 */
	public final void loadLoop() {
		if (loadQueue.size() > 0) {
			int defID = loadQueue.poll();
			if (defID != -1 && map.get(defID) == null) {
				loadDefinition(defID);
			}
		}
	}

	/**
	 * Requests the specified definition for the specified client from this
	 * server.
	 * 
	 * @param client the client that requested the definition
	 * @param def the definition requested
	 */
	public final void requestDefinition(final int client,
			final int def) {
		DefStorage<E> stor = getStorage(def);
		if (stor != null && (stor.empty || stor.def != null)) {
			sendDefinitionTo(client, def, stor.def);
		}
	}

	/**
	 * Sends the specified definition storage to the given client.
	 * 
	 * @param client the client to send the definition to
	 * @param def the definition to send, or <code>null</code> if empty
	 * @param defID the definition id
	 * @return if the packet was sent
	 */
	protected abstract boolean sendDefinitionTo(
			final int client, final int defID,
			final E def);

	/**
	 * Loads the specified definition, and returns <code>true</code> if
	 * successfully loaded.
	 * 
	 * @param defID the definition id to load
	 * @return if the request was made
	 */
	protected abstract boolean loadDefinition(int defID);

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
	 * Gets the server instance bound to this loader.
	 * 
	 * @return the server instance
	 */
	protected final Server getServer() {
		return server;
	}
}
