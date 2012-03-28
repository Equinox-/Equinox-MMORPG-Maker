package com.pi.server.net;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.packet.Packet;

public class DataWorker extends Thread {
    private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
    private NetServer server;

    public DataWorker(NetServer server) {
	super(server.getThreadGroup(), "NetDataWorker");
	this.server = server;
	start();
    }

    public void processData(NetServerClient socket, byte[] data, int count) {
	byte[] dataCopy = new byte[count];
	System.arraycopy(data, 4, dataCopy, 0, count);
	synchronized (queue) {
	    queue.add(new ServerDataEvent(socket, dataCopy));
	    queue.notify();
	}
    }

    @Override
    public void run() {
	ServerDataEvent dataEvent;

	while (server.isConnected()) {
	    synchronized (queue) {
		if (queue.isEmpty()) {
		    try {
			queue.wait();
		    } catch (InterruptedException e) {
		    }
		} else {
		    try {
			dataEvent = queue.remove(0);
			PacketInputStream pIn = new PacketInputStream(
				new ByteArrayInputStream(dataEvent.data));
			Packet pack = Packet.getPacket(server.getLog(), pIn);
			pIn.close();
			server.getLog().finest(
				"Recieved " + pack.getName() + " on "
					+ dataEvent.socket.getID());
			dataEvent.socket.getNetHandler().processPacket(pack);
		    } catch (Exception e) {
			server.getLog().printStackTrace(e);
		    }
		}
	    }
	}
    }
}
