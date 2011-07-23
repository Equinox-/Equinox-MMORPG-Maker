package com.pi.server.net;

import com.pi.common.net.client.NetClient;

public interface ClientListener {
    public void clientConnected(NetClient client);

    public void clientDisconnected(NetClient client);
}
