package com.pi.common.net.packet;

import java.io.*;

public class Packet0Disconnect extends Packet {
    public String reason;
    public String details;

    public Packet0Disconnect() {

    }

    public Packet0Disconnect(String reason, String details) {
	this.reason = reason;
	this.details = details;
    }

    @Override
    protected void writeData(DataOutputStream dOut) throws IOException {
	dOut.writeInt(getID());
	if (reason == null)
	    reason = "";
	if (details == null)
	    details = "";
	super.writeString(dOut, reason);
	super.writeString(dOut, details);
    }

    @Override
    protected void readData(DataInputStream dIn) throws IOException {
	reason = super.readString(dIn);
	details = super.readString(dIn);
    }
}
