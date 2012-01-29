package com.pi.client.net;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.pi.client.Client;

public class ClientSelectorThread extends Thread {
    private final NetClientClient client;
    private final SocketChannel socketChannel;
    private final Selector selector;

    public ClientSelectorThread(NetClientClient client, Client superClient,
	    Socket socket) throws IOException {
	super(superClient.getThreadGroup(), "Net Client Reader Thread");
	this.client = client;
	this.socketChannel = socket.getChannel();
	socketChannel.configureBlocking(false);
	this.selector = Selector.open();
	socketChannel.register(selector, SelectionKey.OP_READ);
	start();
    }

    public void run() {
	while (selector.isOpen() && socketChannel.isOpen()
		&& socketChannel.isConnected()) {
	    try {
		if (selector.select() == 0)
		    continue;

		Iterator<SelectionKey> keys = selector.selectedKeys()
			.iterator();
		while (keys.hasNext()) {
		    SelectionKey key = keys.next();
		    if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
			SocketChannel sc = null;
			sc = (SocketChannel) key.channel();
			int ok = client.readPacket(sc);
			if (ok == -1) {
			    key.cancel();
			    client.dispose();
			}
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace(client.getLog().getErrorStream());
	    }
	}
    }
}
