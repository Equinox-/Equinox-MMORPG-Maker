package com.pi.common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

import com.pi.common.debug.PILogger;
import com.pi.common.net.packet.Packet;

public abstract class NetClient {

	// Speed Monitoring
	private int cacheUploadRate = -1;
	private int cacheDownloadRate = -1;
	private long lastUpdateTime = -1;
	private int sendSinceUpdate = 0;
	private int recieveSinceUpdate = 0;

	private final List<ByteBuffer> sendQueue = new LinkedList<ByteBuffer>();
	protected final SocketChannel socket;
	private final ByteBuffer readBuffer = ByteBuffer
			.allocate(NetConstants.MAX_BUFFER);

	public NetClient(final SocketChannel socket) {
		this.socket = socket;
	}

	public abstract PILogger getLog();

	public abstract void processData(byte[] data, int off, int len);

	public abstract void addWriteRequest();

	public abstract String getSuffix();

	public abstract void wakeSelector();

	public abstract NetHandler getHandler();

	public void read(SelectionKey key) throws IOException {
		int numRead = ((SocketChannel) key.channel()).read(readBuffer);
		if (readBuffer.position() > 4) {
			int len = (readBuffer.get(0) << 24)
					+ ((readBuffer.get(1) & 0xFF) << 16)
					+ ((readBuffer.get(2) & 0xFF) << 8)
					+ (readBuffer.get(3) & 0xFF);
			recieveSinceUpdate += len + 4;
			if (readBuffer.position() >= len + 4) {
				processData(readBuffer.array(), readBuffer.arrayOffset(), len);
				if (readBuffer.position() > len + 4) {
					byte[] temp = new byte[readBuffer.position() - len - 4];
					System.arraycopy(readBuffer.array(),
							readBuffer.arrayOffset() + len + 4, temp, 0,
							temp.length);
					readBuffer.clear();
					readBuffer.put(temp);
				} else {
					readBuffer.clear();
				}
			} else {
				readBuffer.limit(len + 4);
			}
		}

		if (numRead == -1) {
			key.channel().close();
			key.cancel();
			return;
		}
	}

	public void send(Packet pack) {
		getLog().finest(
				"Send " + pack.getName() + " size: " + pack.getLength()
						+ getSuffix());
		try {
			addWriteRequest();
			synchronized (this.sendQueue) {
				int size = pack.getPacketLength();
				ByteBufferOutputStream bO = new ByteBufferOutputStream(size + 4);
				bO.getByteBuffer().put((byte) (size >>> 24));
				bO.getByteBuffer().put((byte) (size >>> 16));
				bO.getByteBuffer().put((byte) (size >>> 8));
				bO.getByteBuffer().put((byte) (size));

				PacketOutputStream pO = new PacketOutputStream(bO);
				pack.writePacket(pO);
				pO.close();
				bO.getByteBuffer().flip();
				sendSinceUpdate += bO.getByteBuffer().capacity();
				sendQueue.add(bO.getByteBuffer());
			}
			wakeSelector();
		} catch (Exception e) {
			getLog().printStackTrace(e);
		}
	}

	public void sendRaw(byte[] packetData) {
		getLog().finest(
				"Sending raw data length " + packetData.length + getSuffix());
		try {
			addWriteRequest();
			synchronized (this.sendQueue) {
				ByteBufferOutputStream bO = new ByteBufferOutputStream(
						packetData.length + 4);
				bO.getByteBuffer().put((byte) (packetData.length >>> 24));
				bO.getByteBuffer().put((byte) (packetData.length >>> 16));
				bO.getByteBuffer().put((byte) (packetData.length >>> 8));
				bO.getByteBuffer().put((byte) (packetData.length));

				bO.getByteBuffer().put(packetData);
				bO.getByteBuffer().flip();
				sendSinceUpdate += bO.getByteBuffer().capacity();
				sendQueue.add(bO.getByteBuffer());
			}
			wakeSelector();
		} catch (Exception e) {
			getLog().printStackTrace(e);
		}
	}

	public boolean isConnected() {
		return socket.isOpen() && socket.isConnected();
	}

	public List<ByteBuffer> getSendQueue() {
		return sendQueue;
	}

	public String getHostAddress() {
		return socket.socket().getInetAddress().getHostAddress();
	}

	private void updateBandwidth() {
		long delta = lastUpdateTime >= 0 ? System.currentTimeMillis()
				- lastUpdateTime : 1000;
		if (delta >= 1000) {
			cacheUploadRate = (int) ((sendSinceUpdate * 1000) / delta);
			cacheDownloadRate = (int) ((recieveSinceUpdate * 1000) / delta);
			lastUpdateTime = System.currentTimeMillis();
			sendSinceUpdate = 0;
			recieveSinceUpdate = 0;
		}
	}

	public int getUploadSpeed() {
		updateBandwidth();
		return cacheUploadRate;
	}

	public int getDownloadSpeed() {
		updateBandwidth();
		return cacheDownloadRate;
	}
}
