package com.pi.common.net.client;

import java.util.Vector;

public class NetProcessingThread extends Thread {
	private boolean running = true;
	public Vector<ClientPacket> lowQueue = new Vector<ClientPacket>(),
			highQueue = new Vector<ClientPacket>();

	public NetProcessingThread(ThreadGroup tGroup, String name) {
		super(tGroup, null, name);
	}

	@Override
	public void run() {
		while (running) {
			if (!highQueue.isEmpty()) {
				highQueue.remove(0).process();
			} else if (!lowQueue.isEmpty()) {
				lowQueue.remove(0).process();
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
