package com.pi.common.net.client;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pi.common.debug.PILogger;
import com.pi.common.net.NetHandler;
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
    protected List<Packet> processQueue = Collections
	    .synchronizedList(new ArrayList<Packet>());
    protected List<Packet> sendQueue = Collections
	    .synchronizedList(new ArrayList<Packet>());
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
	boolean read = false;
	try {
	    int avaliable = dIn.available();
	    if (avaliable > 4 && readLength == null)
		readLength = dIn.readInt();
	    avaliable = dIn.available();
	    if (readLength != null && avaliable >= readLength) {
		getLog().finest("Read pack: " + readLength);
		byte[] data = new byte[readLength];
		netSpeedMonitor.addRecieve(readLength);
		dIn.read(data);
		readLength = null;
		Packet packet = Packet.getPacket(new PacketInputStream(
			new ByteArrayInputStream(data)));
		if (packet != null) {
		    processQueue.add(packet);
		    getLog().finest("Reading packet " + packet.getName());
		    read = true;
		} else {
		    error("End Of Stream", "");
		}
	    }
	} catch (Exception exception) {
	    if (!hasErrored) {
		error(exception);
	    }
	}
	return read;
    }

    public boolean sendQueuedPacket() {
	boolean sent = false;
	try {
	    Packet packet;
	    if (!sendQueue.isEmpty()
		    && (packetTimeout == 0 || System.currentTimeMillis()
			    - sendQueue.get(0).timeStamp >= packetTimeout)) {
		synchronized (syncObject) {
		    packet = (Packet) sendQueue.remove(0);
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
		getLog().finest("Write pack: " + data.length);
		netSpeedMonitor.addSent(data.length + 4);
		sent = true;
	    }
	} catch (Exception e) {
	    if (!hasErrored) {
		error(e);
	    }
	}
	return sent;
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

    public void closeStreams() {
	connected = false;
	try {
	    if (dIn != null)
		dIn.close();
	    dIn = null;
	} catch (Exception e) {
	}
	try {
	    if (dOut != null)
		dOut.close();
	    dOut = null;
	} catch (Exception e) {
	}
	try {
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
		sendQueue.add(packet);
	    }
	}
    }

    public boolean shouldProcessPacket() {
	return !processQueue.isEmpty();
    }

    public void processPacket() {
	for (int i = 0; i < 100 && !processQueue.isEmpty(); i++) {
	    Packet packet = (Packet) processQueue.remove(0);
	    getHandle().processPacket(packet);
	    getLog().finest("Processing packet " + packet.getName());
	}

	if (hasErrored() && processQueue.isEmpty())
	    dispose(errorReason, errorDetails);
    }

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
	while (shouldProcessPacket()) {
	    try {
		Thread.sleep(100l);
	    } catch (InterruptedException e) {
		e.printStackTrace(getLog().getErrorStream());
	    }
	}
	closeStreams();
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
