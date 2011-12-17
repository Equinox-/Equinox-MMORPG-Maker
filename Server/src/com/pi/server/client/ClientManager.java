package com.pi.server.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.pi.common.net.client.NetClient;
import com.pi.server.constants.ServerConstants;

public class ClientManager {
    private Map<Integer, Client> clientMap = Collections
	    .synchronizedMap(new HashMap<Integer, Client>());

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
	if (client != null)
	    client.dispose();
	clientMap.remove(client);
    }

    private int getAvaliableID() {
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
	    clientMap.put(id, c);
	    return id;
	}
	return -1;
    }

    public boolean hasAvaliableSlot() {
	return getAvaliableID() != -1;
    }

    public int getClientID(Client c) {
	for (int it : clientMap.keySet()) {
	    Client client = clientMap.get(it);
	    if (client != null && client == c) {
		return it;
	    }
	}
	return -1;
    }

    public boolean isRegistered(Client c) {
	return getClientID(c) != -1;
    }

    public Map<Integer, Client> registeredClients() {
	return Collections.unmodifiableMap(clientMap);
    }

    public void processPacketLoop() {
	synchronized (clientMap) {
	    for (Client c : clientMap.values()) {
		if (c.getNetClient() != null) {
		    c.getNetClient().processHighPacket();
		    c.getNetClient().processLowPacket();
		}
	    }
	}
    }
}
