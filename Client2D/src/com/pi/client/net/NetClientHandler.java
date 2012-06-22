package com.pi.client.net;

import javax.swing.JOptionPane;

import com.pi.client.Client;
import com.pi.client.entity.ClientEntity;
import com.pi.common.database.Location;
import com.pi.common.database.SectorLocation;
import com.pi.common.debug.PILogger;
import com.pi.common.game.Entity;
import com.pi.common.net.NetHandler;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;
import com.pi.common.net.packet.Packet10EntityDataRequest;
import com.pi.common.net.packet.Packet11LocalEntityID;
import com.pi.common.net.packet.Packet13EntityDef;
import com.pi.common.net.packet.Packet15GameState;
import com.pi.common.net.packet.Packet16EntityMove;
import com.pi.common.net.packet.Packet17Clock;
import com.pi.common.net.packet.Packet2Alert;
import com.pi.common.net.packet.Packet4Sector;
import com.pi.common.net.packet.Packet6BlankSector;
import com.pi.common.net.packet.Packet7EntityTeleport;
import com.pi.common.net.packet.Packet8EntityDispose;
import com.pi.common.net.packet.Packet9EntityData;

public class NetClientHandler extends NetHandler {
	private final ClientNetwork netClient;
	private final Client client;

	public NetClientHandler(ClientNetwork netClient, Client client) {
		this.client = client;
		this.netClient = netClient;
	}

	@Override
	protected PILogger getLog() {
		return client.getLog();
	}

	@Override
	public void process(Packet p) {
	}

	public void process(Packet17Clock p) {
		long ping = (System.currentTimeMillis() - p.clientSendTime) / 2;
		long offset = p.serverSendTime - ping - p.clientSendTime;
		netClient.syncServerClock(ping, offset);
	}

	public void process(Packet0Disconnect p) {
		JOptionPane.showMessageDialog(null, ((Packet0Disconnect) p).reason
				+ "\n" + ((Packet0Disconnect) p).details);
		netClient.dispose();
	}

	public void process(Packet2Alert p) {
		client.getRenderLoop().alert(p.message);
	}

	public void process(Packet4Sector p) {
		client.getWorld().getSectorManager().setSector(p.sector);
	}

	public void process(Packet6BlankSector p) {
		client.getWorld()
				.getSectorManager()
				.flagSectorAsBlank(
						new SectorLocation(p.baseX, p.baseY, p.baseZ));
	}

	public void process(Packet7EntityTeleport p) {
		Entity ent = client.getEntityManager().getEntity(p.entityID);
		if (ent == null) {
			ent = new Entity();
			ent.setEntityID(p.entityID);
			client.getEntityManager().saveEntity(ent);
			client.getNetwork().send(
					Packet10EntityDataRequest.create(p.entityID));
		}
		ent.setLocation(p.moved);
		ent.setLayer(p.entityLayer);
	}

	public void process(Packet16EntityMove p) {
		ClientEntity ent = client.getEntityManager().getEntity(p.entity);
		if (ent == null) {
			ent = new ClientEntity();
			ent.setEntityID(p.entity);
			client.getEntityManager().saveEntity(ent);
			client.getNetwork()
					.send(Packet10EntityDataRequest.create(p.entity));
		}
		Location l = p.apply(ent);
		ent.teleportShort(l);
		if (ent != client.getEntityManager().getLocalEntity())
			ent.forceStartMoveLoop();
	}

	public void process(Packet8EntityDispose p) {
		client.getEntityManager().deRegisterEntity(p.entityID);
	}

	public void process(Packet9EntityData p) {
		Entity ent = client.getEntityManager().getEntity(p.entID);
		if (ent == null) {
			ent = new Entity();
			client.getLog().info("setid:" + ent.setEntityID(p.entID));
		}
		ent.setEntityDef(p.defID);
		ent.setLocation(p.loc);
		ent.setLayer(p.layer);
		client.getEntityManager().saveEntity(ent);
	}

	public void process(Packet11LocalEntityID p) {
		client.getLog().info("LocalID: " + p.entityID);
		client.getEntityManager().setLocalEntityID(p.entityID);
	}

	public void process(Packet13EntityDef p) {
		client.getDefs().getEntityLoader().setDef(p.entityID, p.def);
	}

	public void process(Packet15GameState p) {
		if (p.state != null)
			client.setGameState(p.state);
	}
}
