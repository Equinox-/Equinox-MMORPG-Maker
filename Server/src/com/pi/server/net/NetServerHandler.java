package com.pi.server.net;

import java.util.List;

import com.pi.common.contants.Direction;
import com.pi.common.database.Account;
import com.pi.common.database.Location;
import com.pi.common.debug.PILogger;
import com.pi.common.game.Entity;
import com.pi.common.game.GameState;
import com.pi.common.game.LivingEntity;
import com.pi.common.net.NetHandler;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;
import com.pi.common.net.packet.Packet10EntityDataRequest;
import com.pi.common.net.packet.Packet12EntityDefRequest;
import com.pi.common.net.packet.Packet14ClientMove;
import com.pi.common.net.packet.Packet15GameState;
import com.pi.common.net.packet.Packet16EntityMove;
import com.pi.common.net.packet.Packet17Clock;
import com.pi.common.net.packet.Packet18Health;
import com.pi.common.net.packet.Packet19Attack;
import com.pi.common.net.packet.Packet1Login;
import com.pi.common.net.packet.Packet2Alert;
import com.pi.common.net.packet.Packet3Register;
import com.pi.common.net.packet.Packet5SectorRequest;
import com.pi.server.Server;
import com.pi.server.client.Client;
import com.pi.server.entity.ServerEntity;

/**
 * The packet handler for the network server.
 * 
 * @author Westin
 * 
 */
public class NetServerHandler extends NetHandler {
	/**
	 * The network client this handler is using.
	 */
	private final NetServerClient netClient;
	/**
	 * The server this handler is bound to.
	 */
	private final Server server;

	/**
	 * Creates a packet handler for the given server and net client.
	 * 
	 * @param sServer the server
	 * @param sNetClient the network client
	 */
	public NetServerHandler(final Server sServer,
			final NetServerClient sNetClient) {
		this.netClient = sNetClient;
		this.server = sServer;
	}

	@Override
	protected final PILogger getLog() {
		return server.getLog();
	}

	/**
	 * Gets the client instance this handler is bound to.
	 * 
	 * @return the client
	 */
	private Client getClient() {
		return this.server.getClientManager().getClient(
				netClient.getID());
	}

	@Override
	public void process(final Packet p) {
	}

	/**
	 * Processes the clock packet, id 17.
	 * 
	 * @param p the packet
	 */
	public final void process(final Packet17Clock p) {
		p.serverSendTime = System.currentTimeMillis();
		netClient.send(p);
	}

	/**
	 * Processes a disconnect packet, id 0.
	 * 
	 * @param p the packet
	 */
	public final void process(final Packet0Disconnect p) {
		netClient.dispose(p.reason, p.details);
	}

	/**
	 * Processes the logic packet, id 1.
	 * 
	 * @param p the packet
	 */
	public final void process(final Packet1Login p) {
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

	/**
	 * Processes the registration packet, id 3.
	 * 
	 * @param p the packet
	 */
	public final void process(final Packet3Register p) {
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

	/**
	 * Processes the sector request packet, id 5.
	 * 
	 * @param p the packet
	 */
	public final void process(final Packet5SectorRequest p) {
		server.getWorld().requestSector(netClient.getID(), p);
	}

	/**
	 * Processes the entity data request packet, id 10.
	 * 
	 * @param p the packet
	 */
	public final void process(final Packet10EntityDataRequest p) {
		server.getEntityManager().requestData(netClient.getID(),
				p);
	}

	/**
	 * Processes the entity definition request packet, id 12.
	 * 
	 * @param p the packet
	 */
	public final void process(final Packet12EntityDefRequest p) {
		server.getDefs().getEntityLoader()
				.requestDefinition(netClient.getID(), p.defID);
	}

	/**
	 * Processes the client movement packet, id 14.
	 * 
	 * @param p the packet
	 */
	public final void process(final Packet14ClientMove p) {
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
						&& ent.canMoveIn(server.getWorld(), dir)) {
					ent.teleportShort(l);
					server.getEntityManager().sendEntityMove(
							ent.getEntityID(), origin, l,
							ent.getDir());
				} else {
					cli.getNetClient().send(
							Packet16EntityMove.create(cli
									.getEntity()));
				}
			}
		}
	}

	/**
	 * Processes the attack packet, id 19.
	 * 
	 * @param p the packet
	 */
	public final void process(final Packet19Attack p) {
		Client cli =
				server.getClientManager().getClient(
						netClient.getID());
		if (cli != null) {
			List<ServerEntity> entz =
					server.getEntityManager()
							.getEntitiesAtLocation(
									new Location(
											cli.getEntity().x
													+ cli.getEntity()
															.getDir()
															.getXOff(),
											cli.getEntity().plane,
											cli.getEntity().z
													+ cli.getEntity()
															.getDir()
															.getZOff()));
			for (ServerEntity ent : entz) {
				if (ent.getWrappedEntity() instanceof LivingEntity) {
					LivingEntity lE =
							(LivingEntity) ent
									.getWrappedEntity();
					lE.setHealth(lE.getHealth() - 1);// TODO Based on levels
														// and stuff
					if (lE.getHealth() <= 0) {
						server.getEntityManager()
								.sendEntityDispose(
										ent.getWrappedEntity()
												.getEntityID());
					} else {
						netClient
								.send(Packet18Health.create(lE));
					}
					break;
				}
			}
		}
	}
}
