package com.pi.server.net.client;

import com.pi.common.database.Account;
import com.pi.common.net.NetHandler;
import com.pi.common.net.client.NetClient;
import com.pi.common.net.packet.*;
import com.pi.common.net.packet.Packet2Alert.AlertType;
import com.pi.server.Server;

public class NetServerHandler extends NetHandler {

    private final NetServerClient netClient;
    private final Server server;

    public NetServerHandler(final Server server, final NetServerClient netClient) {
	this.netClient = netClient;
	this.server = server;
    }

    @Override
    public void process(Packet p) {
    }

    public void process(Packet1Login p) {
	Account acc = server.getDatabase().getAccounts().getAccount(p.username);
	if (acc != null) {
	    if (acc.getPasswordHash().equals(p.password)) {
		netClient.bindAccount(acc);
		netClient.send(Packet2Alert.create(AlertType.MAIN_MENU,
			"Login sucessfull"));
	    } else {
		netClient.send(Packet2Alert.create(AlertType.MAIN_MENU,
			"Invalid password"));
	    }
	} else {
	    netClient.send(Packet2Alert.create(AlertType.MAIN_MENU,
		    "That account does not exist"));
	}
    }
}
