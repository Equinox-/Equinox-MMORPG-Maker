package com.pi.server.net;

import com.pi.common.contants.Direction;
import com.pi.common.database.Account;
import com.pi.common.database.Location;
import com.pi.common.debug.PILogger;
import com.pi.common.game.Entity;
import com.pi.common.game.GameState;
import com.pi.common.net.NetHandler;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;
import com.pi.common.net.packet.Packet10EntityDataRequest;
import com.pi.common.net.packet.Packet12EntityDefRequest;
import com.pi.common.net.packet.Packet14ClientMove;
import com.pi.common.net.packet.Packet15GameState;
import com.pi.common.net.packet.Packet16EntityMove;
import com.pi.common.net.packet.Packet17Clock;
import com.pi.common.net.packet.Packet1Login;
import com.pi.common.net.packet.Packet2Alert;
import com.pi.common.net.packet.Packet3Register;
import com.pi.common.net.packet.Packet5SectorRequest;
import com.pi.server.Server;
import com.pi.server.client.Client;

public class NetServerHandler extends NetHandler {
	private final NetServerClient netClient;
	private final Server server;

	public NetServerHandler(final Server server,
			final NetServerClient netClient) {
		this.netClient = netClient;
		this.server = server;
	}

	@Override
	protected PILogger getLog() {
		return server.getLog();
	}

	private Client getClient() {
		return this.server.getClientManager().getClient(
				netClient.getID());
	}

	@Override
	public void process(Packet p) {
	}

	public void process(Packet17Clock p) {
		p.serverSendTime = System.currentTimeMillis();
		netClient.send(p);
	}

	public void process(Packet0Disconnect p) {
		netClient.dispose(p.reason, p.details);
	}

	public void process(Packet1Login p) {
		try {
			Account acc =
					server.getDatabase().getAccounts()
							.getAccount(p.username);
			if (acc != null) {
				if (acc.getPasswordHash().equals(p.password)) {
					getClient().bindAccount(acc);
					netClient.send(Packet15GameState
							.create(GameState.MAIN_GAME));
				} else {
					netClient.send(Packet2Alert
							.create("Invalid password"));
				}
			} else {
				netClient.send(Packet2Alert
						.create("That account does not exist"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void process(Packet3Register p) {
		if (server.getDatabase().getAccounts()
				.addAccount(p.username, p.password)) {
			netClient.send(Packet2Alert
					.create("Sucessfully registered!"));
		} else {
			netClient
					.send(Packet2Alert
							.create("There is already an account by that username!"));
		}
	}

	public void process(Packet5SectorRequest p) {
		server.getWorld().getSectorManager()
				.requestSector(netClient.getID(), p);
	}

	public void process(Packet10EntityDataRequest p) {
		server.getServerEntityManager().requestData(
				netClient.getID(), p);
	}

	public void process(Packet12EntityDefRequest p) {
		server.getDefs().getEntityLoader()
				.requestDef(netClient.getID(), p);
	}

	public void process(Packet14ClientMove p) {
		Client cli =
				server.getClientManager().getClient(
						netClient.getID());
		if (cli != null) {
			Entity ent = cli.getEntity();
			if (ent != null) {
				Location origin =
						new Location(ent.x, ent.plane, ent.z);
				Location l = p.apply(ent);
				int xC = l.x - ent.x;
				int zC = l.z - ent.z;
				Direction dir =
						Direction.getBestDirection(xC, zC);
				if (Location.dist(origin, l) < 2
						&& ent.canMoveIn(server.getWorld()
								.getSectorManager(), dir)) {
					ent.teleportShort(l);
					server.getServerEntityManager()
							.sendEntityMove(ent.getEntityID(),
									origin, l, ent.getDir());
				} else {
					cli.getNetClient().send(
							Packet16EntityMove.create(cli
									.getEntity()));
				}
			}
		}
	}
}
