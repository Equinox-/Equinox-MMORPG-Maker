package com.pi.common.database.def.entity;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.common.game.entity.comp.HealthComponent;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class HealthDefComponent extends EntityDefComponent {
	/**
	 * Current maximum health.
	 */
	private int maxHealth = 1;

	public HealthDefComponent() {
	}

	public HealthDefComponent(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	/**
	 * Sets the maximum health.
	 * 
	 * @param sHealth
	 *            the new maximum health
	 */
	public final void setMaximumHealth(final int sHealth) {
		this.maxHealth = sHealth;
	}

	/**
	 * Gets the maximum health.
	 * 
	 * @return the definition's maximum health
	 */
	public final int getMaximumHealth() {
		return maxHealth;
	}

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeInt(maxHealth);
	}

	@Override
	public int getLength() {
		return SizeOf.INT;
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		maxHealth = pIn.readInt();
	}

	@Override
	public String toString() {
		return "MaxHealth[" + maxHealth + "]";
	}

	@Override
	public EntityComponent createDefaultComponent() {
		return new HealthComponent(maxHealth);
	}
}
