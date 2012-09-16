package com.pi.server.logic;

import com.pi.common.game.entity.comp.HealthComponent;
import com.pi.common.net.packet.Packet;
import com.pi.common.net.packet.Packet18EntityComponent;
import com.pi.server.Server;
import com.pi.server.client.Client;
import com.pi.server.entity.ServerEntity;

public class CombatLogic {
	private final Server server;

	public CombatLogic(final Server svr) {
		this.server = svr;
	}

	public void entityAttackEntity(final ServerEntity attacker,
			final ServerEntity target) {
		HealthComponent targetHealth = (HealthComponent) target
				.getWrappedEntity().getComponent(HealthComponent.class);
		targetHealth.setHealth(targetHealth.getHealth() - 1);
		attacker.updateAttackTime();
		target.setAttacker(attacker.getWrappedEntity().getEntityID());
		Client attackedClient = server.getClientManager().getClientByEntity(
				target.getWrappedEntity().getEntityID());
		Client attackingClient = server.getClientManager().getClientByEntity(
				attacker.getWrappedEntity().getEntityID());
		if (targetHealth.getHealth() <= 0) {
			if (attacker.getAttacker() == target.getWrappedEntity()
					.getEntityID()) {
				attacker.removeAttacker();
			}
			server.getEntityManager().sendEntityDispose(
					target.getWrappedEntity().getEntityID());
			if (attackedClient != null) {
				attackedClient.onEntityDeath();
			}
		} else {
			Packet pack = Packet18EntityComponent.create(
					target.getWrappedEntity(), HealthComponent.class);
			if (attackingClient != null) {
				attackingClient.getNetClient().send(pack);
			}
			if (attackedClient != null) {
				attackedClient.getNetClient().send(pack);
			}
		}
	}
}
