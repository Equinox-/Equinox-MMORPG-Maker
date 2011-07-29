package com.pi.server.net.client;

import com.pi.common.database.Account;
import com.pi.common.database.Sector;
import com.pi.common.net.NetHandler;
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

    public void process(Packet0Disconnect p) {
	netClient.dispose(p.reason, p.details);
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

    public void process(Packet3Register p) {
	if (server.getDatabase().getAccounts()
		.addAccount(p.username, p.password)) {
	    netClient.send(Packet2Alert.create(AlertType.MAIN_MENU,
		    "Sucessfully registered!"));
	} else {
	    netClient.send(Packet2Alert.create(AlertType.MAIN_MENU,
		    "There is already an account by that username!"));
	}
    }

    public void process(Packet5SectorRequest p) {
	server.getWorld().getSectorManager()
		.requestSector(netClient.getID(), p);
    }
}
