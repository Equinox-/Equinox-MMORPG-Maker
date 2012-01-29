package com.pi.common.net.client;

import com.pi.common.net.PacketHeap;

public class NetProcessingThread extends Thread {
    private boolean running = true;
    public PacketHeap<ClientPacket> lowQueue = new PacketHeap<ClientPacket>();
    public PacketHeap<ClientPacket> highQueue = new PacketHeap<ClientPacket>();

    public NetProcessingThread(ThreadGroup tGroup, String name) {
	super(tGroup, null, name);
    }

    @Override
    public void run() {
	while (running) {
	    if (!highQueue.isEmpty()) {
		highQueue.removeFirst().process();
	    } else if (!lowQueue.isEmpty()) {
		lowQueue.removeFirst().process();
	    }
	}
    }

    @SuppressWarnings("deprecation")
    public void dispose() {
	running = false;
	try {
	    join();
	} catch (InterruptedException e) {
	    stop();
	}

    }
}