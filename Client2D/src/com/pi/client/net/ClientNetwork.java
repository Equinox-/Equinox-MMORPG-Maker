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

public class ClientNetwork implements Runnable {
	private InetAddress hostAddress;
	private int port;
	private Selector selector;
	private boolean isRunning = true;

	private Queue<NetChangeRequest> pendingChanges = new LinkedBlockingQueue<NetChangeRequest>();
	private NetHandler handler;
	private ClientDataWorker worker;
	private Client client;

	private Thread thread;

	private NetClientClient netClient;

	private long ping = 0, serverTimeOffset = 0, lastSyncTime = 0,
			sentSyncPacket = 0;

	public ClientNetwork(Client client, String ip, int port) {
		thread = new Thread(client.getThreadGroup(), this);
		thread.setName("ClientSelector");
		try {
			this.hostAddress = InetAddress.getByName(ip);
			this.port = port;
			this.client = client;
			this.selector = initSelector();
			this.handler = new NetClientHandler(this, client);
			this.worker = new ClientDataWorker(this);
			this.netClient = new NetClientClient(this, initiateConnection());
			thread.start();
		} catch (IOException e) {
			client.getLog().printStackTrace(e);
		}
	}

	public long getPing() {
		checkServerClock();
		return ping;
	}

	public long getServerTime() {
		checkServerClock();
		return System.currentTimeMillis() + serverTimeOffset;
	}

	private void checkServerClock() {
		if (System.currentTimeMillis() - lastSyncTime > 30000
				&& System.currentTimeMillis() - sentSyncPacket > 10000) {
			sentSyncPacket = System.currentTimeMillis();
			Packet17Clock p = new Packet17Clock();
			p.clientSendTime = System.currentTimeMillis();
			send(p);
		}
	}

	public void syncServerClock(long ping, long serverTimeOffset) {
		this.ping = ping;
		this.serverTimeOffset = serverTimeOffset;
		lastSyncTime = System.currentTimeMillis();
		getLog().info("Ping: " + ping + "ms");
	}

	public void send(Packet pack) {
		netClient.send(pack);
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				synchronized (this.pendingChanges) {
					while (!pendingChanges.isEmpty()) {
						NetChangeRequest change = pendingChanges.poll();
						switch (change.type) {
						case NetChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket
									.keyFor(this.selector);
							key.interestOps(change.ops);
							break;
						case NetChangeRequest.REGISTER:
							change.socket.register(this.selector, change.ops);
							break;
						}
					}
				}
				if (this.selector.select() > 0) {
					Iterator<SelectionKey> selectedKeys = this.selector
							.selectedKeys().iterator();
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
				break;
			} catch (Exception e) {
				client.getLog().printStackTrace(e);
			}
		}
	}

	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		synchronized (netClient.getSendQueue()) {
			while (!netClient.getSendQueue().isEmpty()) {
				ByteBuffer buf = (ByteBuffer) netClient.getSendQueue().peek();
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

	private void finishConnection(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			client.getLog().printStackTrace(e);
			key.cancel();
			return;
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}

	private SocketChannel initiateConnection() throws IOException {
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
		socketChannel
				.connect(new InetSocketAddress(this.hostAddress, this.port));
		synchronized (this.pendingChanges) {
			this.pendingChanges.add(new NetChangeRequest(socketChannel,
					NetChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
		}

		return socketChannel;
	}

	private Selector initSelector() throws IOException {
		return SelectorProvider.provider().openSelector();
	}

	public void dispose() {
		try {
			isRunning = false;
			selector.wakeup();
			thread.join();
			worker.wakeup();
			worker.join();

			SelectionKey key = netClient.getChannel().keyFor(selector);
			if (key != null)
				key.cancel();

			netClient.getChannel().close();
			selector.close();
		} catch (Exception e) {
			client.getLog().printStackTrace(e);
		}
	}

	public boolean isConnected() {
		return selector.isOpen()
				&& (netClient == null || netClient.isConnected()) && isRunning;
	}

	public NetHandler getNetHandler() {
		return handler;
	}

	public PILogger getLog() {
		return client.getLog();
	}

	public ThreadGroup getThreadGroup() {
		return client.getThreadGroup();
	}

	public DataWorker getWorker() {
		return worker;
	}

	public void wakeSelector() {
		selector.wakeup();
	}

	public void addChangeRequest(NetChangeRequest netChangeRequest) {
		pendingChanges.add(netChangeRequest);
	}
}