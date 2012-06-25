package com.pi.client.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi.client.Client;
import com.pi.common.debug.PILogger;
import com.pi.common.net.DataWorker;
import com.pi.common.net.NetChangeRequest;
import com.pi.common.net.NetHandler;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet17Clock;

/**
 * The main client network class providing the selector threads and data worker
 * for the client's network model.
 * 
 * @author Westin
 * 
 */
public class ClientNetwork extends Thread {
	/**
	 * The time for a clock update to expire, and to send a clock update.
	 */
	private static final int CLOCK_SYNC_TIME = 30000;
	/**
	 * The time for a clock packet to expire, and to resend the packet.
	 */
	private static final int CLOCK_RESEND_TIME = 10000;

	/**
	 * The current host address.
	 */
	private InetAddress hostAddress;
	/**
	 * The current port.
	 */
	private int port;
	/**
	 * The selector instance.
	 */
	private Selector selector;

	/**
	 * The queue that manages changes to the channel's interested operations and
	 * selector registration.
	 */
	private Queue<NetChangeRequest> pendingChanges =
			new LinkedBlockingQueue<NetChangeRequest>();
	/**
	 * The packet handler.
	 */
	private NetHandler handler;
	/**
	 * The data worker that processes data.
	 */
	private ClientDataWorker worker;
	/**
	 * The Client instance this network is bound to.
	 */
	private Client client;

	/**
	 * The NetClientClient instance.
	 */
	private NetClientClient netClient;

	/**
	 * Server time and ping information.
	 */
	private long ping = -1, serverTimeOffset = 0,
			lastSyncTime = 0, sentSyncPacket = 0;

	/**
	 * Shutdown flag for the thread.
	 */
	private boolean isRunning = false;

	/**
	 * Creates a network model and tries to establish a connection to the given
	 * IP on the provided port.
	 * 
	 * @param sClient the client
	 * @param sIp the IP address to establish a connection on. See
	 *            {@link InetAddress#getByName(String)}.
	 * @param sPort the port to establish a connection on.
	 */
	public ClientNetwork(final Client sClient, final String sIp,
			final int sPort) {
		super(sClient.getThreadGroup(), "ClientNetSelector");
		try {
			this.hostAddress = InetAddress.getByName(sIp);
			this.port = sPort;
			this.client = sClient;
			this.selector =
					SelectorProvider.provider().openSelector();
			this.handler = new NetClientHandler(this, client);
			this.worker = new ClientDataWorker(this);
			this.netClient =
					new NetClientClient(this,
							initiateConnection());
			start();
		} catch (IOException e) {
			client.getLog().printStackTrace(e);
		}
	}

	/**
	 * Gets the current ping of the network, or <code>-1</code> if unknown.
	 * 
	 * @return the current ping
	 */
	public final long getPing() {
		checkServerClock();
		return ping;
	}

	/**
	 * Gets the current server time, accommodating for packet travel time. This
	 * will be identical to client time if the offset has not been calculated.
	 * 
	 * @return the server time
	 */
	public final long getServerTime() {
		checkServerClock();
		return System.currentTimeMillis() + serverTimeOffset;
	}

	/**
	 * Checks to see if the current time information has expired, and attempts
	 * to update the information if it has.
	 */
	private void checkServerClock() {
		if (System.currentTimeMillis() - lastSyncTime >= CLOCK_SYNC_TIME
				&& System.currentTimeMillis() - sentSyncPacket >= CLOCK_RESEND_TIME) {
			sentSyncPacket = System.currentTimeMillis();
			Packet17Clock p = new Packet17Clock();
			p.clientSendTime = System.currentTimeMillis();
			send(p);
		}
	}

	/**
	 * Set the server clock data to the given ping and time offset.
	 * 
	 * @param sPing the ping in milliseconds
	 * @param sServerTimeOffset the time offset in milliseconds
	 */
	public final void syncServerClock(final long sPing,
			final long sServerTimeOffset) {
		this.ping = sPing;
		this.serverTimeOffset = sServerTimeOffset;
		lastSyncTime = System.currentTimeMillis();
		getLog().info("Ping: " + ping + "ms");
	}

	/**
	 * Utility method to send a packet.
	 * 
	 * @see NetClientClient#send(Packet)
	 * @param pack the packet to send
	 */
	public final void send(final Packet pack) {
		netClient.send(pack);
	}

	@Override
	public final void run() {
		while (selector.isOpen() && isRunning) {
			try {
				synchronized (this.pendingChanges) {
					while (!pendingChanges.isEmpty()) {
						NetChangeRequest change =
								pendingChanges.poll();
						switch (change.type) {
						case NetChangeRequest.CHANGEOPS:
							SelectionKey key =
									change.socket
											.keyFor(this.selector);
							key.interestOps(change.ops);
							break;
						case NetChangeRequest.REGISTER:
							change.socket.register(
									this.selector, change.ops);
							break;
						default:
							break;
						}
					}
				}
				if (this.selector.select() > 0) {
					Iterator<SelectionKey> selectedKeys =
							this.selector.selectedKeys()
									.iterator();
					while (selectedKeys.hasNext()) {
						SelectionKey key = selectedKeys.next();
						selectedKeys.remove();
						if (!key.isValid()) {
							continue;
						}
						if (key.isConnectable()) {
							this.finishConnection(key);
						} else if (key.isReadable()) {
							netClient.read(key);
						} else if (key.isWritable()) {
							this.write(key);
						}
					}
				}
			} catch (ClosedSelectorException e) {
				isRunning = false;
				break;
			} catch (Exception e) {
				client.getLog().printStackTrace(e);
			}
		}
	}

	/**
	 * Writes any queued packets to the socket channel attached to this key.
	 * 
	 * @param key the key to write on
	 * @throws IOException if an error is thrown
	 */
	private void write(final SelectionKey key)
			throws IOException {
		SocketChannel socketChannel =
				(SocketChannel) key.channel();
		synchronized (netClient.getSendQueue()) {
			while (!netClient.getSendQueue().isEmpty()) {
				ByteBuffer buf =
						(ByteBuffer) netClient.getSendQueue()
								.peek();
				socketChannel.write(buf);
				if (buf.remaining() > 0) {
					break;
				}
				netClient.getSendQueue().poll();
			}

			if (netClient.getSendQueue().isEmpty()) {
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	/**
	 * Finish the connection requested by this selection key.
	 * 
	 * @param key the selection key
	 * @throws IOException if an error occurs
	 */
	private void finishConnection(final SelectionKey key)
			throws IOException {
		SocketChannel socketChannel =
				(SocketChannel) key.channel();
		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			client.getLog().printStackTrace(e);
			key.cancel();
			return;
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}

	/**
	 * Creates a connection to the IP address and port specified by
	 * {@link ClientNetwork#hostAddress} and {@link ClientNetwork#port}.
	 * 
	 * @return the created SocketChannel
	 * @throws IOException if there was a connection error
	 */
	private SocketChannel initiateConnection()
			throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel.connect(new InetSocketAddress(
				this.hostAddress, this.port));
		synchronized (this.pendingChanges) {
			this.pendingChanges.add(new NetChangeRequest(
					socketChannel, NetChangeRequest.REGISTER,
					SelectionKey.OP_CONNECT));
		}

		return socketChannel;
	}

	/**
	 * Dispose and stop the selector thread, and the worker thread.
	 */
	public final void dispose() {
		try {
			isRunning = false;
			selector.wakeup();
			join();
			worker.wakeup();
			worker.join();

			SelectionKey key =
					netClient.getChannel().keyFor(selector);
			if (key != null) {
				key.cancel();
			}

			netClient.getChannel().close();
			selector.close();
		} catch (Exception e) {
			client.getLog().printStackTrace(e);
		}
	}

	/**
	 * Checks the current network status.
	 * 
	 * @return <code>true</code> if the network is connected.
	 */
	public final boolean isConnected() {
		return selector.isOpen()
				&& (netClient == null || netClient.isConnected())
				&& isRunning;
	}

	/**
	 * Gets the packet handler for this network model.
	 * 
	 * @return the NetHandler instance
	 */
	public final NetHandler getNetHandler() {
		return handler;
	}

	/**
	 * Gets the logger instance for logging events.
	 * 
	 * @return the logger
	 */
	public final PILogger getLog() {
		return client.getLog();
	}

	/**
	 * Gets the data worker for this network model.
	 * 
	 * @return the data worker.
	 */
	public final DataWorker getWorker() {
		return worker;
	}

	/**
	 * Utility method to access {@link Selector#wakeup()}.
	 */
	public final void wakeSelector() {
		selector.wakeup();
	}

	/**
	 * Adds the given NetChangeRequest to the pending changes queue.
	 * 
	 * @param netChangeRequest the request to add
	 */
	public final void addChangeRequest(
			final NetChangeRequest netChangeRequest) {
		pendingChanges.add(netChangeRequest);
	}
}