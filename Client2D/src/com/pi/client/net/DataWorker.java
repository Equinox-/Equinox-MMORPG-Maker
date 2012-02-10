package com.pi.client.net;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import com.pi.client.Client;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.packet.Packet;

public class DataWorker extends Thread {
    private List<byte[]> queue = new LinkedList<byte[]>();
    private NetClient client;

    public DataWorker(NetClient client) {
	super(client.getThreadGroup(), "NetDataWorker");
	this.client = client;
	this.start();
    }

    public void processData(byte[] data, int count) {
	byte[] dataCopy = new byte[count];
	System.arraycopy(data, 0, dataCopy, 0, count);
	synchronized (queue) {
	    queue.add(dataCopy);
	    queue.notify();
	}
    }

    @Override
    public void run() {
	byte[] dataEvent;
	while (client.isConnected()) {
	    synchronized (queue) {
		if (queue.isEmpty()) {
		    try {
			queue.wait();
		    } catch (InterruptedException e) {
		    }
		} else {
		    try {
			dataEvent = (byte[]) queue.remove(0);
			PacketInputStream pIn = new PacketInputStream(
				new ByteArrayInputStream(dataEvent));
			client.getNetHandler().processPacket(
				Packet.getPacket(client.getLog(), pIn));
		    } catch (Exception e) {
			client.getLog().printStackTrace(e);
		    }
		}
	    }
	}
    }
}
