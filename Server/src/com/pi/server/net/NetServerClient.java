package com.pi.server.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import com.pi.common.net.NetHandler;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.packet.Packet;
import com.pi.server.Server;

public class NetServerClient {
    private final List<ByteBuffer> sendQueue = new LinkedList<ByteBuffer>();
    private final SocketChannel socket;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    private final Server server;
    private final NetHandler handler;

    private int clientID = -1;

    public NetServerClient(final Server server, final SocketChannel socket) {
	this.socket = socket;
	this.server = server;
	this.handler = new NetServerHandler(server, this);
    }

    public void send(Packet pack) {
    }

    public void bindToID(int id) {
	this.clientID = id;
    }

    public boolean isQuitting() {
	return false;
    }

    public void dispose(String reason, String details) {
    }

    public void dispose() {

    }

    // Thread accessible
    public void write(SelectionKey key) throws IOException {
	SocketChannel socketChannel = (SocketChannel) key.channel();
	if (socketChannel == socket) {
	    synchronized (sendQueue) {
		while (!sendQueue.isEmpty()) {
		    ByteBuffer buf = sendQueue.get(0);
		    socketChannel.write(buf);
		    if (buf.remaining() > 0) {
			break;
		    }
		    sendQueue.remove(0);
		}

		if (sendQueue.isEmpty()) {
		    key.interestOps(SelectionKey.OP_READ);
		}
	    }
	}
    }

    public void read(SelectionKey key) throws IOException {
	SocketChannel socketChannel = (SocketChannel) key.channel();
	this.readBuffer.clear();
	int numRead;
	try {
	    numRead = socketChannel.read(this.readBuffer);
	} catch (IOException e) {
	    key.cancel();
	    socketChannel.close();
	    dispose();
	    return;
	}
	if (numRead == -1) {
	    key.channel().close();
	    key.cancel();
	    return;
	}
	PacketInputStream pIn = new PacketInputStream(new ByteArrayInputStream(
		readBuffer.array()));
	handler.processPacket(Packet.getPacket(server.getLog(), pIn));
    }

    public int getID() {
	return clientID;
    }

    public boolean isConnected() {
	return false;
    }

    public String getHostAddress() {
	return socket.socket().getInetAddress().getHostAddress();
    }

    public NetHandler getNetHandler() {
	return handler;
    }
}
