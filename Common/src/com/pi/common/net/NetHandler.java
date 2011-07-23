package com.pi.common.net;

import java.lang.reflect.Method;

import com.pi.common.net.packet.Packet;

public abstract class NetHandler {
    protected abstract void process(Packet p);

    public final void processPacket(Packet p) {
	Class<? extends NetHandler> myClass = getClass();
	for (Class<? extends Packet> clazz : Packet.idMapping.values()) {
	    if (p.getClass().equals(clazz)) {
		try {
		    Method m = myClass.getMethod("process", clazz);
		    m.invoke(this, clazz.cast(p));
		} catch (Exception e) {
		    e.printStackTrace();
		    process(p);
		}
		return;
	    }
	}
	process(p);
    }
}
