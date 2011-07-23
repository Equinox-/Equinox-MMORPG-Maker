package com.pi.common.net.client;

import java.io.*;
import java.net.Socket;
import java.util.*;

import com.pi.common.PILogger;
import com.pi.common.net.NetHandler;
import com.pi.common.net.packet.Packet;

public abstract class NetClient {
    protected static long packetTimeout = 0; // 0 to disable

    protected int id;
    protected Socket sock;
    protected DataInputStream dIn;
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
    protected NetClientProcessingThread netProcessor;

    protected void connect(int id, Socket sock, NetHandler netHandle) {
	this.sock = sock;
	this.id = id;
	if (sock != null && sock.isConnected()) {
	    try {
		this.dIn = new DataInputStream(this.sock.getInputStream());
		this.dOut = new DataOutputStream(new BufferedOutputStream(
			this.sock.getOutputStream(), 5120)); // 5kb buffer
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    this.netReader = new NetReaderThread(this);
	    this.netWriter = new NetWriterThread(this);
	    this.netProcessor = new NetClientProcessingThread(this);
	    this.netReader.start();
	    this.netWriter.start();
	    this.netHandle = netHandle;
	    this.netProcessor.start();
	}
    }

    public NetHandler getHandle() {
	return netHandle;
    }

    public int getID() {
	return id;
    }

    public boolean isConnected() {
	return connected;
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
	    Packet packet = Packet.getPacket(dIn);
	    if (packet != null) {
		processQueue.add(packet);
		getLog().finer("Reading: " + packet.getID());
		read = true;
	    } else {
		error("End Of Stream", "");
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
		getLog().finer("Sending: " + packet.getID());
		packet.writePacket(dOut);
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
	exception.printStackTrace();
	this.error("Internal Exception", exception.toString());
    }

    public void error(String reason, String details) {
	if (connected) {
	    hasErrored = true;
	    errorReason = reason;
	    errorDetails = details;
	    new NetClientDisposalThread(this).start();
	    connected = false;
	    try {
		dIn.close();
		dIn = null;
	    } catch (Exception e) {
	    }
	    try {
		dOut.close();
		dOut = null;
	    } catch (Exception e) {
	    }
	    try {
		sock.close();
		sock = null;
	    } catch (Exception e) {
	    }
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

    public NetClientProcessingThread getNetProcessor() {
	return netProcessor;
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
	    getLog().finer("Processing: " + packet.getID());
	}

	if (hasErrored() && processQueue.isEmpty())
	    dispose(errorReason, errorDetails);
    }

    public void dispose() {
	quitting = true;
	new NetClientDisposalThread(this).start();
    }

    public void dispose(String reason, String details) {
	dispose();
    }

    public Socket getSocket() {
	return sock;
    }

    public abstract PILogger getLog();

    @Override
    public String toString() {
	return "NetClient: (id="
		+ id
		+ " socket="
		+ (sock != null ? ("(addr="
			+ sock.getInetAddress().getHostAddress() + " port="
			+ sock.getPort() + " localport=" + sock.getLocalPort() + ")")
			: "null") + ")";
    }
}
