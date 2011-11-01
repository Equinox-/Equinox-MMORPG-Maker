package com.pi.server.net.client;

import com.pi.common.database.Account;
import com.pi.common.database.EntityDef;
import com.pi.common.game.Entity;
import com.pi.common.net.NetHandler;
import com.pi.common.net.packet.*;
import com.pi.common.net.packet.Packet2Alert.AlertType;
import com.pi.server.Server;
import com.pi.server.client.Client;
import com.pi.server.constants.Configuration;

public class NetServerHandler extends NetHandler {
    private final NetServerClient netClient;
    private final Server server;

    public NetServerHandler(final Server server, final NetServerClient netClient) {
	this.netClient = netClient;
	this.server = server;
    }

    private Client getClient() {
	return this.server.getClientManager().getClient(netClient.getID());
    }

    @Override
    public void process(Packet p) {
    }

    public void process(Packet0Disconnect p) {
	netClient.dispose(p.reason, p.details);
    }

    public void process(Packet1Login p) {
	try {
	    Account acc = server.getDatabase().getAccounts()
		    .getAccount(p.username);
	    if (acc != null) {
		if (acc.getPasswordHash().equals(p.password)) {
		    if (acc.getEntityDef() == null) {
			EntityDef def = Configuration.spawn_def;
			acc.setSavedEntity(def);
		    }
		    getClient().bindAccount(acc);
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
	} catch (Exception e) {
	    e.printStackTrace();
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
