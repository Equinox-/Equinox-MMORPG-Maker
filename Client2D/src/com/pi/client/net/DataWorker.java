package com.pi.client.net;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

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

    public void processData(byte[] data, int offset, int count) {
	byte[] dataCopy = new byte[count];
	System.arraycopy(data, offset + 4, dataCopy, 0, count);
	synchronized (queue) {
	    queue.add(dataCopy);
	    queue.notify();
	}
    }

    public void wakeup() {
	synchronized (queue) {
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
			Packet pack = Packet.getPacket(client.getLog(), pIn);
			pIn.close();
			if (pack != null) {
			    client.getLog()
				    .finest("Recieved " + pack.getName());
			    client.getNetHandler().processPacket(pack);
			} else {
			    client.getLog().severe("Recieved null packet!");
			}
		    } catch (Exception e) {
			client.getLog().printStackTrace(e);
		    }
		}
	    }
	}
    }
}
