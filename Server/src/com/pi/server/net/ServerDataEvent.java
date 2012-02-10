package com.pi.server.net;

class ServerDataEvent {
    public NetServerClient socket;
    public byte[] data;

    public ServerDataEvent(NetServerClient socket, byte[] data) {
	this.socket = socket;
	this.data = data;
    }
}
