package com.pi.common.net;

import java.lang.reflect.Method;

import com.pi.common.net.packet.Packet;

public abstract class NetHandler {
    protected abstract void process(Packet p);

    public final void processPacket(Packet p) {
	Class<? extends NetHandler> myClass = getClass();
	try {
	    Method m = myClass.getMethod("process", p.getClass());
	    m.invoke(this, p.getClass().cast(p));
	    return;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	process(p);
    }
}
