package com.pi.client.net;

import javax.swing.JOptionPane;

import com.pi.client.Client;
import com.pi.client.entity.ClientEntity;
import com.pi.common.database.Location;
import com.pi.common.database.SectorLocation;
import com.pi.common.debug.PILogger;
import com.pi.common.game.Entity;
import com.pi.common.game.EntityType;
import com.pi.common.game.LivingEntity;
import com.pi.common.net.NetHandler;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;
import com.pi.common.net.packet.Packet10EntityDataRequest;
import com.pi.common.net.packet.Packet11LocalEntityID;
import com.pi.common.net.packet.Packet13EntityDef;
import com.pi.common.net.packet.Packet15GameState;
import com.pi.common.net.packet.Packet16EntityMove;
import com.pi.common.net.packet.Packet17Clock;
import com.pi.common.net.packet.Packet18Health;
import com.pi.common.net.packet.Packet21EntityFace;
import com.pi.common.net.packet.Packet2Alert;
import com.pi.common.net.packet.Packet4Sector;
import com.pi.common.net.packet.Packet6BlankSector;
import com.pi.common.net.packet.Packet7EntityTeleport;
import com.pi.common.net.packet.Packet8EntityDispose;
import com.pi.common.net.packet.Packet9EntityData;

/**
 * A packet handler class for the client's network model.
 * 
 * @author Westin
 * 
 */
public class NetClientHandler extends NetHandler {
	/**
	 * The client network instance.
	 */
	private final ClientNetwork netClient;
	/**
	 * The client instance.
	 */
	private final Client client;

	/**
	 * Create a packet handler for the specified ClientNetwork instance and
	 * Client.
	 * 
	 * @param sNetClient the network instance
	 * @param sClient the client instance
	 */
	public NetClientHandler(final ClientNetwork sNetClient,
			final Client sClient) {
		this.client = sClient;
		this.netClient = sNetClient;
	}

	@Override
	protected final PILogger getLog() {
		return client.getLog();
	}

	@Override
	public void process(final Packet p) {
	}

	/**
	 * Processes the clock packet, id 17.
	 * 
	 * @param p the clock packet
	 */
	public final void process(final Packet17Clock p) {
		long ping =
				(System.currentTimeMillis() - p.clientSendTime) / 2;
		long offset = p.serverSendTime - ping - p.clientSendTime;
		netClient.syncServerClock(ping, offset);
	}

	/**
	 * Processes the disconnect/kick packet, id 0.
	 * 
	 * @param p the disconnect packet
	 */
	public final void process(final Packet0Disconnect p) {
		JOptionPane.showMessageDialog(null,
				((Packet0Disconnect) p).reason + "\n"
						+ ((Packet0Disconnect) p).details);
		netClient.dispose();
	}

	/**
	 * Processes the alert packet, id 2.
	 * 
	 * @param p the alert packet
	 */
	public final void process(final Packet2Alert p) {
		client.getRenderLoop().alert(p.message);
	}

	/**
	 * Processes the sector data packet, id 4.
	 * 
	 * @param p the sector data packet
	 */
	public final void process(final Packet4Sector p) {
		client.getWorld().setSector(p.sector);
	}

	/**
	 * Processes the blank sector packet, id 6.
	 * 
	 * @param p the blank sector packet
	 */
	public final void process(final Packet6BlankSector p) {
		client.getWorld().flagSectorAsBlank(
				new SectorLocation(p.baseX, p.baseY, p.baseZ));
	}

	/**
	 * Processes the entity teleport packet, id 7.
	 * 
	 * @param p the entity teleport packet
	 */
	public final void process(final Packet7EntityTeleport p) {
		ClientEntity cEnt =
				client.getEntityManager().getEntity(p.entityID);
		if (cEnt == null) {
			client.getNetwork()
					.send(Packet10EntityDataRequest
							.create(p.entityID));
			/*
			 * Entity ent = new Entity(); ent.setEntityID(p.entityID);
			 * ent.setLocation(p.moved); ent.setLayer(p.entityLayer);
			 * client.getEntityManager().saveEntity(ent);
			 */
			return;
		}
		cEnt.getWrappedEntity().setLocation(p.moved);
		cEnt.getWrappedEntity().setLayer(p.entityLayer);
	}

	/**
	 * Processes the entity movement packet, id 16.
	 * 
	 * @param p the entity movement packet
	 */
	public final void process(final Packet16EntityMove p) {
		ClientEntity cEnt =
				client.getEntityManager().getEntity(p.entity);
		if (cEnt == null) {
			client.getNetwork().send(
					Packet10EntityDataRequest.create(p.entity));
			/*
			 * Entity ent = new Entity(); ent .setEntityID (p.entity); client
			 * .getEntityManager ( ).saveEntity( ent); cEnt = client
			 * .getEntityManager ( ).getEntity(p .entity);
			 */
			return;
		}
		Entity ent = cEnt.getWrappedEntity();
		Location l = p.apply(ent);
		ent.teleportShort(l);
		if (cEnt != client.getEntityManager().getLocalEntity()) {
			cEnt.forceStartMoveLoop();
		}
	}

	/**
	 * Processes the entity disposal packet, id 8.
	 * 
	 * @param p the entity disposal packet
	 */
	public final void process(final Packet8EntityDispose p) {
		client.getEntityManager().deRegisterEntity(p.entityID);
	}

	/**
	 * Processes the entity data packet, id 9.
	 * 
	 * @param p the entity data packet
	 */
	public final void process(final Packet9EntityData p) {
		ClientEntity cEnt =
				client.getEntityManager().getEntity(p.entID);
		Entity ent;
		if (cEnt == null) {
			ent =
					client.getEntityManager().registerEntity(
							p.eType, p.entID);
		} else {
			ent = cEnt.getWrappedEntity();
			EntityType cType = EntityType.getEntityType(ent);
			if (cType != p.eType) {
				client.getLog().severe(
						"Entity type conflict on entity "
								+ p.entID
								+ ".  The registered type is "
								+ cType.name()
								+ ", when it should be "
								+ p.eType.name());
			}
		}
		ent.setEntityDef(p.defID);
		ent.setLocation(p.loc);
		ent.setLayer(p.layer);
	}

	/**
	 * Processes the local entity id packet, id 11.
	 * 
	 * @param p the local entity packet.
	 */
	public final void process(final Packet11LocalEntityID p) {
		client.getLog().info("LocalID: " + p.entityID);
		client.getEntityManager().setLocalEntityID(p.entityID);
	}

	/**
	 * Processes the entity definition packet, id 13.
	 * 
	 * @param p the entity definition packet
	 */
	public final void process(final Packet13EntityDef p) {
		client.getDefs().getEntityLoader()
				.setDef(p.entityID, p.def);
	}

	/**
	 * Processes the game state packet, id 15.
	 * 
	 * @param p the game state packet
	 */
	public final void process(final Packet15GameState p) {
		if (p.state != null) {
			client.setGameState(p.state);
		}
	}

	/**
	 * Processes an entity health update packet, id 18.
	 * 
	 * @param p the health packet
	 */
	public final void process(final Packet18Health p) {
		ClientEntity cEnt =
				client.getEntityManager().getEntity(p.entityID);
		if (cEnt == null) {
			client.getNetwork()
					.send(Packet10EntityDataRequest
							.create(p.entityID));
			/*
			 * Entity ent = new Entity(); ent .setEntityID (p.entity); client
			 * .getEntityManager ( ).saveEntity( ent); cEnt = client
			 * .getEntityManager ( ).getEntity(p .entity);
			 */
			return;
		}
		if (cEnt.getWrappedEntity() instanceof LivingEntity) {
			((LivingEntity) cEnt.getWrappedEntity())
					.setHealth(p.health);
		}
	}

	/**
	 * Processes an entity face packet, id 21.
	 * 
	 * @param p the entity face packet
	 */
	public final void process(final Packet21EntityFace p) {
		ClientEntity cEnt =
				client.getEntityManager().getEntity(p.entityID);
		if (cEnt == null) {
			client.getNetwork()
					.send(Packet10EntityDataRequest
							.create(p.entityID));
			return;
		}
		if (cEnt.getWrappedEntity() instanceof LivingEntity) {
			cEnt.getWrappedEntity().setDir(p.face);
		}
	}
}
