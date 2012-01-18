package com.pi.common.net.packet;

import java.io.IOException;

import com.pi.common.net.client.PacketInputStream;
import com.pi.common.net.client.PacketOutputStream;

public class Packet2Alert extends Packet {
    public static enum AlertType {
	MAIN_MENU, NONE;
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
	dOut.writeInt(alertType.ordinal());
	dOut.writeString(message);
    }

    @Override
    protected void readData(PacketInputStream dIn) throws IOException {
	int al = dIn.readInt();
	if (al >= 0 && al < AlertType.values().length)
	    alertType = AlertType.values()[al];
	else
	    alertType = AlertType.NONE;
	message = dIn.readString();
    }

    @Override
    public int getID() {
	return 2;
    }
}
