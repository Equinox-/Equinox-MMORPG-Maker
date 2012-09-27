package com.pi.common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import com.pi.common.debug.PILogger;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.PacketManager;

/**
 * An abstract class providing the central methods for processing packets using
 * a net client's net handler and a separate processing thread.
 * 
 * @author Westin
 * 
 */
public abstract class DataWorker extends Thread {
	/**
	 * The queue used for processing packets.
	 */
	private volatile Queue<DataEvent> queue =
			new PriorityBlockingQueue<DataEvent>();

	/**
	 * Creates a data worker in the provided thread group.
	 * 
	 * @param t the thread group to register this thread with
	 */
	public DataWorker(final ThreadGroup t) {
		super(t, "NetDataWorker");
		start();
	}

	/**
	 * Processes data for the given net client.
	 * 
	 * @param socket the socket this data was received on
	 * @param data the data received
	 * @param off the array offset of the data
	 * @param count the received bytes count
	 * @throws IOException if there is a problem reading identifying the packet
	 */
	public final void processData(final NetClient socket,
			final byte[] data, final int off, final int count)
			throws IOException {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, off, dataCopy, 0, count);
		synchronized (queue) {
			queue.add(new DataEvent(socket, dataCopy, this));
			queue.notify();
		}
	}

	/**
	 * Checks if this thread should continue running. Make sure to call the
	 * {@link #wakeup()} method after setting this to <code>false</code>.
	 * 
	 * @return if the thread should continue
	 */
	public abstract boolean isRunning();

	/**
	 * Gets the logger instance assigned to this data worker.
	 * 
	 * @return the logger
	 */
	public abstract PILogger getLog();

	/**
	 * Wakes up this thread, and causes it to continue the currently waiting
	 * thread.
	 */
	public final void wakeup() {
		synchronized (queue) {
			queue.notify();
		}
	}

	@Override
	public final void run() {
		DataEvent dataEvent;

		while (isRunning()) {
			synchronized (queue) {
				if (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
						getLog().printStackTrace(e);
					}
				} else {
					try {
						dataEvent = queue.poll();
						dataEvent.packet.readData(dataEvent.pIn);
						getLog().finest(
								"Received "
										+ dataEvent.packet
												.getName()
										+ dataEvent.socket
												.getSuffix());
						dataEvent.socket.getHandler()
								.processPacket(dataEvent.packet);
					} catch (Exception e) {
						getLog().printStackTrace(e);
					}
				}
			}
		}
	}

	/**
	 * Class describing received data.
	 * 
	 * @author Westin
	 * 
	 */
	public static class DataEvent implements
			Comparable<DataEvent> {
		/**
		 * The net client this data was received on.
		 */
		private NetClient socket;
		/**
		 * A byte buffer wrapping received data.
		 */
		private ByteBuffer data;
		/**
		 * The packet instance describing the received data.
		 */
		private Packet packet;
		/**
		 * The packet input stream currently in use.
		 */
		private PacketInputStream pIn;

		/**
		 * Create a data event for the given socket and data event.
		 * 
		 * @param sSocket the net client
		 * @param sData the received data
		 * @param worker the data worker instance
		 * @throws IOException if there is a problem identifying the packet
		 */
		public DataEvent(final NetClient sSocket,
				final byte[] sData, final DataWorker worker)
				throws IOException {
			this.socket = sSocket;
			this.data = ByteBuffer.wrap(sData);
			this.pIn = new PacketInputStream(this.data);
			this.packet = PacketManager.getPacket(worker.getLog(), pIn);
		}

		@Override
		public final int compareTo(final DataEvent o) {
			return packet.compareTo(o.packet);
		}
	}
}
