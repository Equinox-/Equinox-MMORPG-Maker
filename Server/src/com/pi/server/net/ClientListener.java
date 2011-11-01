package com.pi.server.net;

import com.pi.server.client.Client;

public interface ClientListener {
    public void clientConnected(Client client);

    public void clientDisconnected(Client client);
}
