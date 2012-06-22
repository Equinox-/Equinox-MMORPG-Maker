package com.pi.common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.pi.common.debug.PILogger;
import com.pi.common.net.packet.Packet;

public abstract class DataWorker extends Thread {
	private Queue<DataEvent> queue = new PriorityBlockingQueue<DataEvent>();

	public DataWorker(ThreadGroup t) {
		super(t, "NetDataWorker");
		start();
	}

	public void processData(NetClient socket, byte[] data, int off, int count)
			throws IOException {
		byte[] dataCopy = new byte[count];
		ByteBuffer bb;
		System.arraycopy(data, off + 4, dataCopy, 0, count);
		synchronized (queue) {
			queue.add(new DataEvent(socket, dataCopy, this));
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
						dataEvent = queue.poll();
						dataEvent.packet.readData(dataEvent.pIn);
						getLog().finest(
								"Recieved " + dataEvent.packet.getName()
										+ dataEvent.socket.getSuffix());
						dataEvent.socket.getHandler().processPacket(
								dataEvent.packet);
					} catch (Exception e) {
						getLog().printStackTrace(e);
					}
				}
			}
		}
	}

	public static class DataEvent implements Comparable<DataEvent> {
		public NetClient socket;
		public ByteBuffer data;
		public Packet packet;
		public PacketInputStream pIn;

		public DataEvent(NetClient socket, byte[] data, DataWorker worker)
				throws IOException {
			this.socket = socket;
			this.data = ByteBuffer.wrap(data);
			this.pIn = new PacketInputStream(this.data);
			this.packet = Packet.getPacket(worker.getLog(), pIn);
		}

		@Override
		public int compareTo(DataEvent o) {
			return packet.compareTo(o.packet);
		}
	}
}
