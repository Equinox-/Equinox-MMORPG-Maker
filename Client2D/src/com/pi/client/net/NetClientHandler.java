package com.pi.client.net;

import javax.swing.JOptionPane;

import com.pi.client.Client;
import com.pi.common.database.SectorLocation;
import com.pi.common.game.Entity;
import com.pi.common.net.NetHandler;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet0Disconnect;
import com.pi.common.net.packet.Packet10EntityDataRequest;
import com.pi.common.net.packet.Packet11LocalEntityID;
import com.pi.common.net.packet.Packet13EntityDef;
import com.pi.common.net.packet.Packet15GameState;
import com.pi.common.net.packet.Packet2Alert;
import com.pi.common.net.packet.Packet4Sector;
import com.pi.common.net.packet.Packet6BlankSector;
import com.pi.common.net.packet.Packet7EntityMove;
import com.pi.common.net.packet.Packet8EntityDispose;
import com.pi.common.net.packet.Packet9EntityData;

public class NetClientHandler extends NetHandler {
	private final NetClientClient netClient;
	private final Client client;

	public NetClientHandler(NetClientClient netClient, Client client) {
		this.client = client;
		this.netClient = netClient;
	}

	@Override
	public void process(Packet p) {
	}

	public void process(Packet0Disconnect p) {
		JOptionPane.showMessageDialog(null, ((Packet0Disconnect) p).reason
				+ "\n" + ((Packet0Disconnect) p).details);
		netClient.dispose();
	}

	public void process(Packet2Alert p) {
		client.getDisplayManager().getRenderLoop().alert(p.message);
	}

	public void process(Packet4Sector p) {
		client.getWorld().getSectorManager().setSector(p.sector);
	}

	public void process(Packet6BlankSector p) {
		client.getWorld()
				.getSectorManager()
				.flagSectorAsBlack(
						new SectorLocation(p.baseX, p.baseY, p.baseZ));
	}

	public void process(Packet7EntityMove p) {
		Entity ent = client.getEntityManager().getEntity(p.entityID);
		if (ent == null) {
			ent = new Entity();
			ent.setEntityID(p.entityID);
			client.getNetwork().send(
					Packet10EntityDataRequest.create(p.entityID));
		}
		ent.setLocation(p.moved);
		ent.setLayer(p.entityLayer);
		client.getEntityManager().saveEntity(ent);
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
