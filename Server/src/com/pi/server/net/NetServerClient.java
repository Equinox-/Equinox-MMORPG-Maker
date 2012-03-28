package com.pi.server.net;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import com.pi.common.net.ByteBufferOutputStream;
import com.pi.common.net.NetChangeRequest;
import com.pi.common.net.NetConstants;
import com.pi.common.net.NetHandler;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.Packet;
import com.pi.server.Server;

public class NetServerClient {
    private final List<ByteBuffer> sendQueue = new LinkedList<ByteBuffer>();
    private final SocketChannel socket;
    private final ByteBuffer readBuffer = ByteBuffer.allocate(NetConstants.MAX_BUFFER);
    private final Server server;
    private final NetHandler handler;

    private int clientID = -1;

    public NetServerClient(final Server server, final SocketChannel socket) {
	this.socket = socket;
	this.server = server;
	this.handler = new NetServerHandler(server, this);
    }

    public ByteBuffer getReadBuffer() {
	return readBuffer;
    }

    public void send(Packet pack) {
	server.getLog().finest(
		"Send " + pack.getName() + " size: " + pack.getLength()
			+ " on " + getID());
	try {
	    server.getNetwork().addChangeRequest(
		    new NetChangeRequest(socket, NetChangeRequest.CHANGEOPS,
			    SelectionKey.OP_WRITE));
	    synchronized (this.sendQueue) {
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
		sendQueue.add(bO.getByteBuffer());
	    }
	    server.getNetwork().wakeSelector();
	} catch (Exception e) {
	    server.getLog().printStackTrace(e);
	}
    }

    public void sendRaw(byte[] packetData) {
	server.getLog().finest(
		"Sending raw data length " + packetData.length + " on "
			+ getID());
	try {
	    server.getNetwork().addChangeRequest(
		    new NetChangeRequest(socket, NetChangeRequest.CHANGEOPS,
			    SelectionKey.OP_WRITE));
	    synchronized (this.sendQueue) {
		ByteBufferOutputStream bO = new ByteBufferOutputStream(
			packetData.length + 4);
		bO.getByteBuffer().put((byte) (packetData.length >>> 24));
		bO.getByteBuffer().put((byte) (packetData.length >>> 16));
		bO.getByteBuffer().put((byte) (packetData.length >>> 8));
		bO.getByteBuffer().put((byte) (packetData.length));

		bO.getByteBuffer().put(packetData);
		bO.getByteBuffer().flip();
		sendQueue.add(bO.getByteBuffer());
	    }
	    server.getNetwork().wakeSelector();
	} catch (Exception e) {
	    server.getLog().printStackTrace(e);
	}
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

    public List<ByteBuffer> getSendQueue() {
	return sendQueue;
    }
}
