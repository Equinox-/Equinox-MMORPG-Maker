package com.pi.server.net;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi.common.debug.PILogger;
import com.pi.common.net.DataWorker;
import com.pi.common.net.NetChangeRequest;
import com.pi.server.Server;
import com.pi.server.client.Client;

/**
 * The main server network class providing the main selector thread and data
 * worker.
 * 
 * @author Westin
 * 
 */
public class NetServer extends Thread {
	/**
	 * The port this server is bound to.
	 */
	private int port;
	/**
	 * The socket channel this server uses.
	 */
	private ServerSocketChannel serverChannel;
	/**
	 * The selector this server uses.
	 */
	private Selector selector;
	/**
	 * The data worker this network uses.
	 */
	private DataWorker worker;
	/**
	 * The pending changes on this network server.
	 */
	private Queue<NetChangeRequest> pendingChanges =
			new LinkedBlockingQueue<NetChangeRequest>();
	/**
	 * The server this network is bound to.
	 */
	private Server server;
	/**
	 * If the selector thread is running.
	 */
	private boolean isRunning = true;

	/**
	 * Creates a network server bound to the given server and port.
	 * 
	 * @param sServer the server instance
	 * @param sPort the port to listen on
	 * @throws BindException if there was an error connecting
	 */
	public NetServer(final Server sServer, final int sPort)
			throws BindException {
		super(sServer.getThreadGroup(), "NetSelector");
		try {
			this.server = sServer;
			this.port = sPort;
			this.selector = this.initSelector();
			this.worker = new ServerDataWorker(this);
			start();
		} catch (BindException e) {
			throw e;
		} catch (IOException e) {
			server.getLog().printStackTrace(e);
		}
	}

	@Override
	public final void run() {
		server.getLog().info("Started selector");
		while (isConnected()) {
			server.getClientManager().removeDeadClients();
			try {
				// Process any pending changes
				synchronized (this.pendingChanges) {
					Iterator<NetChangeRequest> changes =
							this.pendingChanges.iterator();
					while (changes.hasNext()) {
						NetChangeRequest change = changes.next();
						switch (change.getType()) {
						case NetChangeRequest.CHANGEOPS:
							SelectionKey key =
									change.getChannel().keyFor(
											this.selector);
							if (key != null && key.isValid()) {
								key.interestOps(change
										.getOperations());
								changes.remove();
							}
						default:
							break;
						}
					}
				}
				this.selector.select();
				Iterator<SelectionKey> selectedKeys =
						this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = selectedKeys.next();
					selectedKeys.remove();
					if (!key.isValid()) {
						continue;
					}
					if (key.isAcceptable()) {
						this.accept(key);
					} else if (key.isReadable()) {
						this.read(key);
					} else if (key.isWritable()) {
						this.write(key);
					}
				}
			} catch (ClosedSelectorException e) {
				server.getLog().info("Closed selector");
				break;
			} catch (Exception e) {
				server.getLog().printStackTrace(e);
			}
		}
		server.getLog().info("Stopped selector");
	}

	/**
	 * Gets the data worker that this server processes data on.
	 * 
	 * @return the data worker
	 */
	public final DataWorker getWorker() {
		return worker;
	}

	/**
	 * Processes an acceptable selection key.
	 * 
	 * @param key the selection key
	 * @throws IOException if an error occurs
	 */
	private void accept(final SelectionKey key)
			throws IOException {
		ServerSocketChannel serverSocketChannel =
				(ServerSocketChannel) key.channel();
		SocketChannel socketChannel =
				serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		Client c =
				new Client(server, new NetServerClient(server,
						socketChannel));
		c.getNetClient().bindClient(c);
		socketChannel.register(this.selector,
				SelectionKey.OP_READ).attach(c);
	}

	/**
	 * Processes a readable selection key.
	 * 
	 * @param key the selection key
	 * @throws IOException if an error occurs
	 */
	private void read(final SelectionKey key) throws IOException {
		SocketChannel socketChannel =
				(SocketChannel) key.channel();
		try {
			NetServerClient cli =
					((Client) key.attachment()).getNetClient();
			cli.read(key);
		} catch (IOException e) {
			key.cancel();
			socketChannel.close();
			return;
		}
	}

	/**
	 * Processes a writable selection key.
	 * 
	 * @param key the selection key
	 * @throws IOException if an error occurs
	 */
	private void write(final SelectionKey key)
			throws IOException {
		SocketChannel socketChannel =
				(SocketChannel) key.channel();
		NetServerClient c =
				((Client) key.attachment()).getNetClient();
		synchronized (c.getSendQueue()) {
			while (!c.getSendQueue().isEmpty()) {
				ByteBuffer buf =
						(ByteBuffer) c.getSendQueue().peek();
				socketChannel.write(buf);
				if (buf.remaining() > 0) {
					break;
				}
				c.getSendQueue().poll();
			}

			if (c.getSendQueue().isEmpty()) {
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	/**
	 * Creates the server channel and the selector bound to it.
	 * 
	 * @return a new selector
	 * @throws IOException if a creation error occurs
	 */
	private Selector initSelector() throws IOException {
		Selector socketSelector =
				SelectorProvider.provider().openSelector();
		this.serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		InetSocketAddress isa =
				new InetSocketAddress((InetAddress) null,
						this.port);
		serverChannel.socket().bind(isa);
		serverChannel.register(socketSelector,
				SelectionKey.OP_ACCEPT);
		return socketSelector;
	}

	/**
	 * Disposes all the related resources of this network server.
	 */
	public final void dispose() {
		try {
			isRunning = false;
			selector.wakeup();
			join();
			worker.wakeup();
			worker.join();
			selector.close();
			serverChannel.close();
		} catch (Exception e) {
			server.getLog().printStackTrace(e);
		}
	}

	/**
	 * Checks if this server is connected to the network.
	 * 
	 * @return if the network is connected
	 */
	public final boolean isConnected() {
		return selector.isOpen() && serverChannel.isOpen()
				&& isRunning;
	}

	/**
	 * Gets the logger instance bound to this network server.
	 * 
	 * @return the logger
	 */
	public final PILogger getLog() {
		return server.getLog();
	}

	/**
	 * Wakes the selector bound to this server.
	 */
	public final void wakeSelector() {
		selector.wakeup();
	}

	/**
	 * Adds a network change request to the change queue.
	 * 
	 * @param netChangeRequest the change request
	 */
	public final void addChangeRequest(
			final NetChangeRequest netChangeRequest) {
		pendingChanges.add(netChangeRequest);
	}

	/**
	 * Removes a socket channel from this selector.
	 * 
	 * @param s the channel to remove
	 */
	public final void deregisterSocketChannel(
			final SocketChannel s) {
		SelectionKey k = s.keyFor(selector);
		if (k != null) {
			k.cancel();
		}
	}
}
