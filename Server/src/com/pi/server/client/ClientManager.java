package com.pi.server.client;

import com.pi.common.game.ObjectHeap;
import com.pi.server.constants.ServerConstants;

public class ClientManager {
	private ObjectHeap<Client> clientMap = new ObjectHeap<Client>();

	public Client getClient(int id) {
		return clientMap.get(id);
	}

	public void disposeClient(int id) {
		Client c = getClient(id);
		if (c != null)
			c.dispose();
		clientMap.remove(id);
	}

	public void disposeClient(Client client) {
		disposeClient(client.getID());
	}

	public int getAvaliableID() {
		for (int i = 0; i < ServerConstants.MAX_CLIENTS; i++)
			if (clientMap.get(i) == null)
				return i;
		return -1;
	}

	public int registerClient(Client c) {
		int id;
		if ((id = getClientID(c)) >= 0)
			return id;
		id = getAvaliableID();
		if (id != -1) {
			clientMap.set(id, c);
			return id;
		}
		return -1;
	}

	public boolean hasAvaliableSlot() {
		return getAvaliableID() != -1;
	}

	public int getClientID(Client c) {
		/*
		 * for (int i = 0; i<) { Client client = clientMap.get(it); if (client
		 * != null && client == c) { return it; } }
		 */
		return -1;
	}

	public boolean isRegistered(Client c) {
		return getClientID(c) != -1;
	}

	public ObjectHeap<Client> registeredClients() {
		return clientMap;
	}
}
