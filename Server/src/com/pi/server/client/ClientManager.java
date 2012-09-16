package com.pi.server.client;

import com.pi.common.util.ObjectHeap;
import com.pi.server.constants.ServerConstants;

/**
 * The client manager for registering and disposing client instances.
 * 
 * @see com.pi.server.client.Client
 * @author Westin
 * 
 */
public class ClientManager {
	/**
	 * The mapping that stores the client instances.
	 */
	private ObjectHeap<Client> clientMap =
			new ObjectHeap<Client>();

	/**
	 * Gets the client with the given identification number, or
	 * <code>null</code> if there isn't a client registered.
	 * 
	 * @param id the identification number
	 * @return the client instance
	 */
	public final Client getClient(final int id) {
		return clientMap.get(id);
	}

	/**
	 * Gets the client with the given entity identification number, or
	 * <code>null</code> if there isn't a client registered with the given
	 * entity.
	 * 
	 * @param entityID the identification number
	 * @return the client instance
	 */
	public final Client getClientByEntity(final int entityID) {
		for (Client c : clientMap) {
			if (c.getEntity() != null
					&& c.getEntity().getEntityID() == entityID) {
				return c;
			}
		}
		return null;
	}

	/**
	 * Disposes the client linked to the given identification number and removes
	 * the client from the mapping.
	 * 
	 * @param id the identification number
	 */
	public final void disposeClient(final int id) {
		Client c = getClient(id);
		if (c != null) {
			c.dispose();
			clientMap.remove(id);
		}
	}

	/**
	 * Disposes the given client and removes it from the mapping.
	 * 
	 * @param client the client
	 */
	public final void disposeClient(final Client client) {
		disposeClient(client.getID());
	}

	/**
	 * Gets an identification number that doesn't have a client bound to it.
	 * 
	 * @return the available id, or <code>-1</code> if there isn't one available
	 */
	public final int getAvailableID() {
		for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
			if (clientMap.get(i) == null) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Disposes all clients that don't have a connected network model.
	 */
	public final void removeDeadClients() {
		for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
			Client c = clientMap.get(i);
			if (c != null
					&& (c.getNetClient() == null || !c
							.getNetClient().isConnected())) {
				disposeClient(c);
			}
		}
	}

	/**
	 * Updates the handshake confirmation packets of all connected clients.
	 */
	public final void updateHandshakes() {
		for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++) {
			Client c = clientMap.get(i);
			if (c != null
					&& (c.getNetClient() == null || !c
							.getNetClient().isConnected())) {
				c.getNetClient().checkHandshakes();
			}
		}
	}

	/**
	 * Registers the client to this manager, returning the identification number
	 * it was registered to, or <code>-1</code> if there wasn't an available
	 * slot.
	 * 
	 * @see #getAvailableID()
	 * @param c the client to register
	 * @return the identification number
	 */
	public final int registerClient(final Client c) {
		int id = getClientID(c);
		if (id >= 0) {
			return id;
		}
		id = getAvailableID();
		if (id != -1) {
			clientMap.set(id, c);
			return id;
		}
		return -1;
	}

	/**
	 * Checks if there is an available slot for a client to be registered on.
	 * 
	 * @return <code>true</code> if there is an available slot, or
	 *         <code>false</code> if not
	 */
	public final boolean hasAvaliableSlot() {
		return getAvailableID() != -1;
	}

	/**
	 * Scans the client map for the same client as the one provided, and returns
	 * the identification number, or <code>-1</code> if not found.
	 * 
	 * @param c the client to scan for
	 * @return the id number
	 */
	public final int getClientID(final Client c) {
		for (int i = 0; i < clientMap.capacity(); i++) {
			Client client = clientMap.get(i);
			if (client != null && client == c) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the mapping this client manager uses. This should never be
	 * modified.
	 * 
	 * @return the mapping
	 */
	public final ObjectHeap<Client> registeredClients() {
		return clientMap;
	}

	/**
	 * Removes the client with the given ID from the registry.
	 * 
	 * @param id the identification number
	 */
	public final void removeFromRegistry(final int id) {
		clientMap.remove(id);
	}
}
