package com.pi.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pi.client.Client;
import com.pi.common.debug.PILogger;
import com.pi.common.net.ByteBufferOutputStream;
import com.pi.common.net.NetChangeRequest;
import com.pi.common.net.NetConstants;
import com.pi.common.net.NetHandler;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.Packet;

public class NetClient extends Thread {
    private InetAddress hostAddress;
    private int port;
    private Selector selector;
    private SocketChannel socket;
    private ByteBuffer readBuffer = ByteBuffer
	    .allocate(NetConstants.MAX_BUFFER);
    private boolean isRunning = true;

    private List<NetChangeRequest> pendingChanges = new LinkedList<NetChangeRequest>();
    private List<ByteBuffer> writeQueue = new LinkedList<ByteBuffer>();
    private NetHandler handler;
    private DataWorker worker;
    private Client client;

    public NetClient(Client client, String ip, int port) {
	super(client.getThreadGroup(), "ClientSelector");
	try {
	    this.hostAddress = InetAddress.getByName(ip);
	    this.port = port;
	    this.client = client;
	    this.selector = initSelector();
	    this.socket = initiateConnection();
	    this.handler = new NetClientHandler(this, client);
	    this.worker = new DataWorker(this);
	    start();
	} catch (IOException e) {
	    client.getLog().printStackTrace(e);
	}
    }

    public void send(Packet pack) {
	getLog().finest("Send " + pack.getName() + " len: " + pack.getLength());
	try {
	    this.pendingChanges.add(new NetChangeRequest(socket,
		    NetChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
	    synchronized (this.writeQueue) {
		int size = pack.getPacketLength();
		ByteBufferOutputStream bO = new ByteBufferOutputStream(size + 4);
		bO.getByteBuffer().put((byte) (size >>> 24));
		bO.getByteBuffer().put((byte) (size >>> 16));
		bO.getByteBuffer().put((byte) (size >>> 8));
		bO.getByteBuffer().put((byte) (size));

		PacketOutputStream pO = new PacketOutputStream(bO);
		pack.writePacket(pO);
		pO.close();
		bO.getByteBuffer().flip();
		writeQueue.add(bO.getByteBuffer());
	    }
	    this.selector.wakeup();
	} catch (Exception e) {
	    client.getLog().printStackTrace(e);
	}
    }

    @Override
    public void run() {
	while (isConnected()) {
	    try {
		synchronized (this.pendingChanges) {
		    Iterator<NetChangeRequest> changes = this.pendingChanges
			    .iterator();
		    while (changes.hasNext()) {
			NetChangeRequest change = changes.next();
			switch (change.type) {
			case NetChangeRequest.CHANGEOPS:
			    SelectionKey key = socket.keyFor(this.selector);
			    key.interestOps(change.ops);
			    break;
			case NetChangeRequest.REGISTER:
			    socket.register(this.selector, change.ops);
			    break;
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
		    if (key.isConnectable()) {
			this.finishConnection(key);
		    } else if (key.isReadable()) {
			this.read(key);
		    } else if (key.isWritable()) {
			this.write(key);
		    }
		}
	    } catch (ClosedSelectorException e) {
		break;
	    } catch (Exception e) {
		client.getLog().printStackTrace(e);
	    }
	}
    }

    private void read(SelectionKey key) throws IOException {
	SocketChannel socketChannel = (SocketChannel) key.channel();
	int numRead;
	try {
	    numRead = socketChannel.read(readBuffer);
	    if (readBuffer.position() > 4) {
		int len = (readBuffer.get(0) << 24)
			+ ((readBuffer.get(1) & 0xFF) << 16)
			+ ((readBuffer.get(2) & 0xFF) << 8)
			+ (readBuffer.get(3) & 0xFF);
		getLog().info("size: " + len);
		if (readBuffer.position() >= len + 4) {
		    this.worker.processData(readBuffer.array(),
			    readBuffer.arrayOffset(), len);
		    if (readBuffer.position() > len + 4) {
			byte[] temp = new byte[readBuffer.position() - len - 4];
			System.arraycopy(readBuffer.array(),
				readBuffer.arrayOffset() + len + 4, temp, 0,
				temp.length);
			readBuffer.clear();
			readBuffer.put(temp);
		    } else {
			readBuffer.clear();
		    }
		} else {
		    readBuffer.limit(len + 4);
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

	synchronized (this.writeQueue) {
	    while (!writeQueue.isEmpty()) {
		ByteBuffer buf = writeQueue.get(0);
		socketChannel.write(buf);
		if (buf.remaining() > 0) {
		    break;
		}
		writeQueue.remove(0);
	    }

	    if (writeQueue.isEmpty()) {
		key.interestOps(SelectionKey.OP_READ);
	    }
	}
    }

    private void finishConnection(SelectionKey key) throws IOException {
	SocketChannel socketChannel = (SocketChannel) key.channel();
	try {
	    socketChannel.finishConnect();
	} catch (IOException e) {
	    client.getLog().printStackTrace(e);
	    key.cancel();
	    return;
	}
	key.interestOps(SelectionKey.OP_WRITE);
    }

    private SocketChannel initiateConnection() throws IOException {
	// Create a non-blocking socket channel
	SocketChannel socketChannel = SocketChannel.open();
	socketChannel.configureBlocking(false);

	// Kick off connection establishment
	socketChannel
		.connect(new InetSocketAddress(this.hostAddress, this.port));

	// Queue a channel registration since the caller is not the
	// selecting thread. As part of the registration we'll register
	// an interest in connection events. These are raised when a channel
	// is ready to complete connection establishment.
	synchronized (this.pendingChanges) {
	    this.pendingChanges.add(new NetChangeRequest(socketChannel,
		    NetChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
	}

	return socketChannel;
    }

    private Selector initSelector() throws IOException {
	// Create a new selector
	return SelectorProvider.provider().openSelector();
    }

    public void dispose() {
	try {
	    isRunning = false;
	    notify();
	    join();
	    worker.notify();
	    worker.join();
	    selector.close();
	    socket.close();
	} catch (Exception e) {
	    client.getLog().printStackTrace(e);
	}
    }

    public boolean isConnected() {
	return selector.isOpen() && socket.isOpen() && isRunning;
    }

    public NetHandler getNetHandler() {
	return handler;
    }

    public PILogger getLog() {
	return client.getLog();
    }
}