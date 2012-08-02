package com.pi.server.def;

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
	private ObjectHeap<E> map = new ObjectHeap<E>();

	/**
	 * Creates a definition loader with the specified server as the controller.
	 * 
	 * @param sServer the controller
	 */
	public DefinitionsLoader(final Server sServer) {
		this.server = sServer;
	}

	/**
	 * Gets the definition instance and returns it.
	 * 
	 * @param defID the definition id
	 * @return the definitions instance, or <code>null</code> if empty.
	 */
	public final E getDef(final int defID) {
		return map.get(defID);
	}

	/**
	 * Assigns a definition instance to a definition ID. Should be invoked by
	 * the handling method for data packet received from the server.
	 * 
	 * @param defID the definition id
	 * @param eDef the definition instance
	 */
	public final void setDef(final int defID, final E eDef) {
		map.set(defID, eDef);
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
		E defI = getDef(def);
		if (defI != null) {
			sendDefinitionTo(client, def, defI);
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
			final int client, final int defID, final E def);

	/**
	 * Loads all definitions that this loader can load.
	 * 
	 */
	protected abstract void loadAllDefinitions();

	/**
	 * Obtains a heap representing all the loaded definitions.
	 * 
	 * @return the loaded heap
	 */
	public final ObjectHeap<E> loadedMap() {
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
