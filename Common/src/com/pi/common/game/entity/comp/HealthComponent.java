package com.pi.common.game.entity.comp;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class HealthComponent extends EntityComponent {
	/**
	 * Current health.
	 */
	private int health = 1;

	public HealthComponent() {
	}

	public HealthComponent(int health) {
		this.health = health;
	}

	/**
	 * Sets the current health.
	 * 
	 * @param sHealth the new health
	 */
	public final void setHealth(final int sHealth) {
		this.health = sHealth;
	}

	/**
	 * Gets the current health.
	 * 
	 * @return the entity's health
	 */
	public final int getHealth() {
		return health;
	}

	@Override
	public void writeData(PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(health);
	}

	@Override
	public int getLength() {
		return SizeOf.INT;
	}

	@Override
	public void readData(PacketInputStream pIn)
			throws IOException {
		health = pIn.readInt();
	}

	@Override
	public String toString() {
		return "Health[" + health + "]";
	}
}
