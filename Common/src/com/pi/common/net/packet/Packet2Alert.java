package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet2Alert extends Packet {
    public static enum AlertType {
	MAIN_MENU, NONE;
	private String getMessage(String message) {
	    return name() + ((char) 128) + message;
	}

	public static AlertType matchType(String name) {
	    for (AlertType t : values())
		if (t.name().equalsIgnoreCase(name))
		    return t;
	    return AlertType.NONE;
	}
    }

    public static Packet2Alert create(AlertType type, String message) {
	Packet2Alert p = new Packet2Alert();
	p.alertType = type;
	p.message = message;
	return p;
    }

    public String message;
    public AlertType alertType = AlertType.NONE;

    @Override
    protected void writeData(PacketOutputStream dOut) throws IOException {
	dOut.writeString(alertType.getMessage(message));
    }

    @Override
    protected void readData(PacketInputStream dIn) throws IOException {
	String[] data = dIn.readString().split(new String(new char[] { 128 }),2);
	if (data.length > 1) {
	    message = "";
	    for (int i = 1; i < data.length; i++) {
		message += data[i];
		if (i < data.length - 1) {
		    message += (char) 128;
		}
	    }
	    alertType = AlertType.matchType(data[0]);
	} else {
	    message = data[0];
	    alertType = AlertType.NONE;
	}
    }
}
