package com.pi.client.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.pi.common.debug.PILogger;
import com.pi.common.net.NetChangeRequest;
import com.pi.common.net.NetClient;
import com.pi.common.net.NetHandler;

public class NetClientClient extends NetClient {
	private ClientNetwork network;

	public NetClientClient(ClientNetwork net, SocketChannel socket) {
		super(socket);
		this.network = net;
	}

	@Override
	public PILogger getLog() {
		return network.getLog();
	}

	@Override
	public void processData(byte[] data, int off, int len) {
		try {
			network.getWorker().processData(this, data, off, len);
		} catch (IOException e) {
			getLog().printStackTrace(e);
		}
	}

	@Override
	public void addWriteRequest() {
		network.addChangeRequest(new NetChangeRequest(socket,
				NetChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
	}

	@Override
	public String getSuffix() {
		return "";
	}

	@Override
	public void wakeSelector() {
		network.wakeSelector();
	}

	@Override
	public NetHandler getHandler() {
		return network.getNetHandler();
	}

	@Override
	public String toString() {
		return "NetClient[" + super.getHostAddress() + "]";
	}

	public SocketChannel getChannel() {
		return socket;
	}
}
