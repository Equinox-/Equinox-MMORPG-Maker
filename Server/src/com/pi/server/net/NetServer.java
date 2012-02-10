package com.pi.server.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pi.common.debug.PILogger;
import com.pi.common.net.NetChangeRequest;
import com.pi.server.Server;
import com.pi.server.client.Client;

public class NetServer extends Thread {
    private InetAddress hostAddress;
    private int port;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    private DataWorker worker;
    private List<NetChangeRequest> pendingChanges = new LinkedList<NetChangeRequest>();
    private Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();
    private Server server;

    public NetServer(Server server, int port) {
	super(server.getThreadGroup(), "NetSelector");
	try {
	    this.hostAddress = InetAddress.getLocalHost();
	    this.server = server;
	    this.port = port;
	    this.selector = this.initSelector();
	    this.worker = new DataWorker(this);
	    start();
	} catch (IOException e) {
	    server.getLog().printStackTrace(e);
	}
    }

    public void send(SocketChannel socket, byte[] data) {
	synchronized (this.pendingChanges) {
	    this.pendingChanges.add(new NetChangeRequest(socket,
		    NetChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
	    synchronized (this.pendingData) {
		List<ByteBuffer> queue = this.pendingData.get(socket);
		if (queue == null) {
		    queue = new ArrayList<ByteBuffer>();
		    this.pendingData.put(socket, queue);
		}
		queue.add(ByteBuffer.wrap(data));
	    }
	}
	this.selector.wakeup();
    }

    @Override
    public void run() {
	while (true) {
	    try {
		// Process any pending changes
		synchronized (this.pendingChanges) {
		    Iterator<NetChangeRequest> changes = this.pendingChanges
			    .iterator();
		    while (changes.hasNext()) {
			NetChangeRequest change = changes.next();
			switch (change.type) {
			case NetChangeRequest.CHANGEOPS:
			    SelectionKey key = change.socket
				    .keyFor(this.selector);
			    key.interestOps(change.ops);
			}
		    }
		    this.pendingChanges.clear();
		}
		this.selector.select();
		Iterator<SelectionKey> selectedKeys = this.selector
			.selectedKeys().iterator();
		while (selectedKeys.hasNext()) {
		    SelectionKey key = selectedKeys.next();
		    selectedKeys.remove();

		    if (!key.isValid()) {
			continue;
		    }
		    String data = (key.isAcceptable() ? "Accept;" : "")
			    + (key.isConnectable() ? "Connect;" : "")
			    + (key.isReadable() ? "Read;" : "")
			    + (key.isWritable() ? "Write" : "");
		    if (data.length() > 0)
			server.getLog().info(data);
		    if (key.isAcceptable()) {
			this.accept(key);
		    } else if (key.isReadable()) {
			this.read(key);
		    } else if (key.isWritable()) {
			this.write(key);
		    }
		}
	    } catch (ClosedSelectorException e) {
		break;
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    private void accept(SelectionKey key) throws IOException {
	server.getLog().info("ACCEPT");
	ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
		.channel();
	SocketChannel socketChannel = serverSocketChannel.accept();
	socketChannel.configureBlocking(false);
	Client c = new Client(server,
		new NetServerClient(server, socketChannel));
	socketChannel.register(this.selector, SelectionKey.OP_READ).attach(c);
    }

    private void read(SelectionKey key) throws IOException {
	SocketChannel socketChannel = (SocketChannel) key.channel();
	this.readBuffer.clear();
	int numRead;
	try {
	    numRead = socketChannel.read(this.readBuffer);
	} catch (IOException e) {
	    key.cancel();
	    socketChannel.close();
	    return;
	}

	if (numRead == -1) {
	    key.channel().close();
	    key.cancel();
	    return;
	}
	this.worker.processData((NetServerClient) key.attachment(),
		this.readBuffer.array(), numRead);
    }

    private void write(SelectionKey key) throws IOException {
	SocketChannel socketChannel = (SocketChannel) key.channel();

	synchronized (this.pendingData) {
	    List<ByteBuffer> queue = this.pendingData.get(socketChannel);
	    while (!queue.isEmpty()) {
		ByteBuffer buf = (ByteBuffer) queue.get(0);
		socketChannel.write(buf);
		if (buf.remaining() > 0) {
		    break;
		}
		queue.remove(0);
	    }

	    if (queue.isEmpty()) {
		key.interestOps(SelectionKey.OP_READ);
	    }
	}
    }

    private Selector initSelector() throws IOException {
	Selector socketSelector = SelectorProvider.provider().openSelector();
	this.serverChannel = ServerSocketChannel.open();
	serverChannel.configureBlocking(false);
	InetSocketAddress isa = new InetSocketAddress(this.hostAddress,
		this.port);
	serverChannel.socket().bind(isa);
	serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
	return socketSelector;
    }

    public void dispose() {
	try {
	    selector.close();
	    serverChannel.close();
	    notify();
	    join();
	    worker.notify();
	    worker.join();
	} catch (Exception e) {
	    server.getLog().printStackTrace(e);
	}
    }

    public boolean isConnected() {
	return selector.isOpen() && serverChannel.isOpen();
    }

    public PILogger getLog() {
	return server.getLog();
    }
}
