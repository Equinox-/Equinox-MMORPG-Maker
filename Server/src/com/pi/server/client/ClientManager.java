package com.pi.server.client;

import com.pi.server.constants.ServerConstants;

public class ClientManager {
    private Client[] clients = new Client[ServerConstants.MAX_CLIENTS];

    public Client getClient(int id) {
	if (id >= 0 && id < clients.length)
	    return clients[id];
	return null;
    }

    public void disposeClient(int id) {
	Client c = getClient(id);
	if (c != null)
	    c.dispose();
    }

    private int getAvaliableID() {
	for (int i = 0; i < clients.length; i++)
	    if (clients[i] == null)
		return i;
	return -1;
    }

    public boolean registerClient(Client c) {
	if (isRegistered(c))
	    return true;
	int id = getAvaliableID();
	if (id != -1) {
	    clients[id] = c;
	    return true;
	}
	return false;
    }

    public int getClientID(Client c) {
	for (int id = 0; id < clients.length; id++) {
	    Client cli = clients[id];
	    if (cli != null && cli.equals(c))
		return id;
	}
	return -1;
    }

    public boolean isRegistered(Client c) {
	return getClientID(c) != -1;
    }
}
