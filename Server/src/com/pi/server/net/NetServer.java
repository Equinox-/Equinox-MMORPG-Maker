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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pi.common.debug.PILogger;
import com.pi.common.net.NetChangeRequest;
import com.pi.server.Server;
import com.pi.server.client.Client;

public class NetServer extends Thread {
    private int port;
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private DataWorker worker;
    private List<NetChangeRequest> pendingChanges = new LinkedList<NetChangeRequest>();
    private Server server;

    public NetServer(Server server, int port) {
	super(server.getThreadGroup(), "NetSelector");
	try {
	    this.server = server;
	    this.port = port;
	    this.selector = this.initSelector();
	    this.worker = new DataWorker(this);
	    start();
	} catch (IOException e) {
	    server.getLog().printStackTrace(e);
	}
    }

    @Override
    public void run() {
	server.getLog().info("Started selector");
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
		    if (key.isAcceptable()) {
			this.accept(key);
		    } else if (key.isReadable()) {
			this.read(key);
		    } else if (key.isWritable()) {
			this.write(key);
		    }
		}
	    } catch (ClosedSelectorException e) {
		server.getLog().info("Closed selector");
		break;
	    } catch (Exception e) {
		server.getLog().printStackTrace(e);
	    }
	}
	server.getLog().info("Stopped selector");
    }

    public DataWorker getWorker() {
	return worker;
    }

    private void accept(SelectionKey key) throws IOException {
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
	int numRead;
	try {
	    NetServerClient cli = ((Client) key.attachment()).getNetClient();
	    numRead = socketChannel.read(cli.getReadBuffer());
	    if (cli.getReadBuffer().position() > 4) {
		int len = (cli.getReadBuffer().get(0) << 24)
			+ ((cli.getReadBuffer().get(1) & 0xFF) << 16)
			+ ((cli.getReadBuffer().get(2) & 0xFF) << 8)
			+ (cli.getReadBuffer().get(3) & 0xFF);
		getLog().info("size: " + len);
		cli.getReadBuffer().limit(len + 4);
		if (cli.getReadBuffer().position() >= len + 4) {
		    this.worker.processData(cli, cli.getReadBuffer().array(),
			    len);
		    cli.getReadBuffer().clear();
		}
	    }

	    if (numRead == -1) {
		key.channel().close();
		key.cancel();
		return;
	    }
	} catch (IOException e) {
	    key.cancel();
	    socketChannel.close();
	    return;
	}
    }

    private void write(SelectionKey key) throws IOException {
	SocketChannel socketChannel = (SocketChannel) key.channel();
	NetServerClient c = ((Client) key.attachment()).getNetClient();
	synchronized (c.getSendQueue()) {
	    while (!c.getSendQueue().isEmpty()) {
		ByteBuffer buf = (ByteBuffer) c.getSendQueue().get(0);
		socketChannel.write(buf);
		if (buf.remaining() > 0) {
		    break;
		}
		c.getSendQueue().remove(0);
	    }

	    if (c.getSendQueue().isEmpty()) {
		key.interestOps(SelectionKey.OP_READ);
	    }
	}
    }

    private Selector initSelector() throws IOException {
	Selector socketSelector = SelectorProvider.provider().openSelector();
	this.serverChannel = ServerSocketChannel.open();
	serverChannel.configureBlocking(false);
	InetSocketAddress isa = new InetSocketAddress((InetAddress) null,
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

    public void wakeSelector() {
	selector.wakeup();
    }

    public void addChangeRequest(NetChangeRequest netChangeRequest) {
	pendingChanges.add(netChangeRequest);
    }
}
