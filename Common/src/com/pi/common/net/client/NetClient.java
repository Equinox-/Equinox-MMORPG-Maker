package com.pi.common.net.client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.pi.common.debug.PILogger;
import com.pi.common.net.NetHandler;
import com.pi.common.net.PacketHeap;
import com.pi.common.net.packet.Packet;

public abstract class NetClient {
    protected static long packetTimeout = 0; // 0 to disable

    protected int id = -1;
    protected Socket sock;
    protected DataInputStream dIn;
    private Integer readLength;
    protected DataOutputStream dOut;
    protected boolean connected = true;
    protected boolean quitting = false;
    protected boolean hasErrored = false;
    protected NetReaderThread netReader;
    protected NetWriterThread netWriter;
    protected String errorReason;
    protected String errorDetails;
    protected PacketHeap<Packet> l_sendQueue = new PacketHeap<Packet>();
    protected PacketHeap<Packet> h_sendQueue = new PacketHeap<Packet>();
    protected Object syncObject = new Object();
    protected NetHandler netHandle;
    protected NetClientSpeedMonitor netSpeedMonitor;

    protected void connect(Socket sock, NetHandler netHandle) {
	this.sock = sock;
	netSpeedMonitor = new NetClientSpeedMonitor(this);
	if (sock != null && sock.isConnected()) {
	    try {
		sock.setTrafficClass(24);
	    } catch (SocketException e) {

	    }
	    try {
		sock.setSoTimeout(30000);
		this.dIn = new DataInputStream(this.sock.getInputStream());
		this.dOut = new DataOutputStream(new BufferedOutputStream(
			this.sock.getOutputStream(), 5120)); // 5kb buffer
	    } catch (IOException e) {
		e.printStackTrace(getLog().getErrorStream());
	    }
	    this.netHandle = netHandle;
	    this.netReader = new NetReaderThread(this);
	    this.netWriter = new NetWriterThread(this);
	    this.netReader.start();
	    this.netWriter.start();
	}
    }

    public boolean bindToID(int id) {
	if (this.id == -1) {
	    this.id = id;
	    netReader.setName(getID() + " reader thread");
	    netWriter.setName(getID() + " writer thread");
	    getLog().info("Bound client to " + id);
	    return true;
	}
	return false;
    }

    public int getID() {
	return id;
    }

    public NetHandler getHandle() {
	return netHandle;
    }

    public boolean isConnected() {
	return connected && sock != null && sock.isConnected()
		&& !sock.isClosed();
    }

    public boolean isQuitting() {
	return quitting;
    }

    public boolean hasErrored() {
	return hasErrored;
    }

    public boolean readPacket() {
	try {
	    int avaliable = dIn.available();
	    if (avaliable > 4 && readLength == null)
		readLength = dIn.readInt();
	    avaliable = dIn.available();
	    if (readLength != null && avaliable >= readLength) {
		byte[] data = new byte[readLength];
		netSpeedMonitor.addRecieve(readLength);
		dIn.readFully(data);
		readLength = null;
		Packet packet = Packet.getPacket(new PacketInputStream(
			new ByteArrayInputStream(data)));
		if (packet != null) {
		    (packet.isHighPriority() ? getHighProcessQueue()
			    : getLowProcessQueue()).addLast(new ClientPacket(this,
			    packet));
		    getLog().finest(
			    "Reading packet " + packet.getName() + ": "
				    + packet.isHighPriority());
		    return true;
		} else {
		    error("End Of Stream", "");
		}
	    }
	} catch (Exception exception) {
	    if (!hasErrored) {
		error(exception);
	    }
	}
	return false;
    }

    public boolean sendQueuedPacket() {
	try {
	    Packet packet;
	    PacketHeap<Packet> sendQueue = (!h_sendQueue.isEmpty() ? h_sendQueue
		    : l_sendQueue);
	    if (!sendQueue.isEmpty()
		    && (packetTimeout == 0 || System.currentTimeMillis()
			    - sendQueue.peakFirst().timeStamp >= packetTimeout)) {
		synchronized (syncObject) {
		    packet = (Packet) sendQueue.removeFirst();
		}
		getLog().finest("Sending packet " + packet.getName());
		byte[] data;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		PacketOutputStream pOut = new PacketOutputStream(bOut);
		packet.writePacket(pOut);
		pOut.close();
		data = bOut.toByteArray();
		bOut.close();
		dOut.writeInt(data.length);
		dOut.write(data);
		dOut.flush();
		netSpeedMonitor.addSent(data.length + 4);
	    }
	    return true;
	} catch (Exception e) {
	    if (!hasErrored) {
		error(e);
	    }
	}
	return false;
    }

    public void error(Exception exception) {
	String s = "";
	for (StackTraceElement e : exception.getStackTrace())
	    s += e.toString();
	this.error("Internal Exception", exception.toString() + "\n" + s);
    }

    public void error(String reason, String details) {
	if (connected) {
	    getLog().severe("Error: " + reason + "\n" + details + "\n");
	    hasErrored = true;
	    errorReason = reason;
	    errorDetails = details;
	    quitting = true;
	    new NetClientDisposalThread(this).start();
	}
    }

    public void shutdownSocket() {
	connected = false;
	try {
	    if (dIn != null)
		dIn.close();
	    dIn = null;
	} catch (Exception e) {
	}
	try {
	    if (dOut != null) {
		dOut.flush();
		dOut.close();
	    }
	    dOut = null;
	} catch (Exception e) {
	}
	try {
	    if (!sock.isInputShutdown())
		sock.shutdownInput();
	    if (!sock.isOutputShutdown())
		sock.shutdownOutput();
	    if (sock != null && !sock.isClosed())
		sock.close();
	    sock = null;
	} catch (Exception e) {
	}
    }

    public void flushOutput() throws IOException {
	if (dOut != null)
	    dOut.flush();
    }

    public NetReaderThread getNetReader() {
	return netReader;
    }

    public NetWriterThread getNetWriter() {
	return netWriter;
    }

    public void send(Packet packet) {
	if (!quitting) {
	    synchronized (syncObject) {
		(packet.isHighPriority() ? h_sendQueue : l_sendQueue)
			.addLast(packet);
	    }
	}
    }

    protected abstract PacketHeap<ClientPacket> getLowProcessQueue();

    protected abstract PacketHeap<ClientPacket> getHighProcessQueue();

    public void processPacket(Packet p) {
	if (p != null)
	    getHandle().processPacket(p);
	if (hasErrored())
	    dispose(errorReason, errorDetails);
    }

    /*
     * public void processLowPacket() { // for (int i = 0; i < 100 &&
     * !processQueue.isEmpty(); i++) { if (h_processQueue.isEmpty() &&
     * !l_processQueue.isEmpty()) { Packet packet = (Packet)
     * l_processQueue.remove(0); getHandle().processPacket(packet); } if
     * (hasErrored() && h_processQueue.isEmpty() && l_processQueue.isEmpty())
     * dispose(errorReason, errorDetails); }
     * 
     * public void processHighPacket() { if (!h_processQueue.isEmpty()) { Packet
     * packet = (Packet) h_processQueue.remove(0);
     * getHandle().processPacket(packet); } if (hasErrored() &&
     * h_processQueue.isEmpty() && l_processQueue.isEmpty())
     * dispose(errorReason, errorDetails); }
     */

    public void dispose() {
	quitting = true;
	new NetClientDisposalThread(this).start();
    }

    @SuppressWarnings("deprecation")
    public void forceDispose() {
	quitting = true;
	if (getNetReader() != null && getNetReader().isAlive())
	    try {
		getNetReader().join();
	    } catch (Exception e) {
		e.printStackTrace(getLog().getErrorStream());
		getNetReader().stop();
	    }
	if (getNetWriter() != null && getNetWriter().isAlive())
	    try {
		getNetWriter().join();
	    } catch (Exception e) {
		e.printStackTrace(getLog().getErrorStream());
		getNetWriter().stop();
	    }
	shutdownSocket();
    }

    public void dispose(String reason, String details) {
	dispose();
    }

    public Socket getSocket() {
	return sock;
    }

    public abstract PILogger getLog();

    public abstract ThreadGroup getThreadGroup();

    @Override
    public String toString() {
	return "NetClient: (socket="
		+ (sock != null ? ("(addr="
			+ sock.getInetAddress().getHostAddress() + " port="
			+ sock.getPort() + " localport=" + sock.getLocalPort() + ")")
			: "null") + ")";
    }

    public int getUploadSpeed() {
	return netSpeedMonitor.getUploadSpeed();
    }

    public int getDownloadSpeed() {
	return netSpeedMonitor.getDownloadSpeed();
    }
}
