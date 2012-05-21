package com.pi.common.net;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

import com.pi.common.debug.PILogger;
import com.pi.common.net.packet.Packet;

public abstract class DataWorker extends Thread {
	private List<DataEvent> queue = new LinkedList<DataEvent>();

	public DataWorker(ThreadGroup t) {
		super(t, "NetDataWorker");
		start();
	}

	public void processData(NetClient socket, byte[] data, int off, int count) {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, off + 4, dataCopy, 0, count);
		synchronized (queue) {
			queue.add(new DataEvent(socket, dataCopy));
			queue.notify();
		}
	}

	public abstract boolean isRunning();

	public abstract PILogger getLog();

	public void wakeup() {
		synchronized (queue) {
			queue.notify();
		}
	}

	@Override
	public void run() {
		DataEvent dataEvent;

		while (isRunning()) {
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
						Packet pack = Packet.getPacket(getLog(), pIn);
						pIn.close();
						getLog().finest(
								"Recieved " + pack.getName()
										+ dataEvent.socket.getSuffix());
						dataEvent.socket.getHandler().processPacket(pack);
					} catch (Exception e) {
						getLog().printStackTrace(e);
					}
				}
			}
		}
	}

	public static class DataEvent {
		public NetClient socket;
		public byte[] data;

		public DataEvent(NetClient socket, byte[] data) {
			this.socket = socket;
			this.data = data;
		}
	}
}
