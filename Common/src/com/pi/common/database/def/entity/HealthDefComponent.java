package com.pi.common.database.def.entity;

import java.io.IOException;

import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.common.game.entity.comp.HealthComponent;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * An entity definition for living entities, provides the maximum health for the
 * entity.
 * 
 * @author westin
 * 
 */
public class HealthDefComponent extends EntityDefComponent {
	/**
	 * Current maximum health.
	 */
	private int maxHealth = 1;

	/**
	 * Creates an empty health definition.
	 */
	public HealthDefComponent() {
	}

	/**
	 * Creates a health definition with the provided maximum health.
	 * 
	 * @param sHealth the maximum health
	 */
	public HealthDefComponent(final int sHealth) {
		this.maxHealth = sHealth;
	}

	/**
	 * Sets the maximum health.
	 * 
	 * @param sHealth the new maximum health
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
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(maxHealth);
	}

	@Override
	public final int getLength() {
		return SizeOf.INT;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		maxHealth = pIn.readInt();
	}

	@Override
	public final String toString() {
		return "MaxHealth[" + maxHealth + "]";
	}

	@Override
	public final EntityComponent[] createDefaultComponents() {
		return new EntityComponent[] { new HealthComponent(
				maxHealth) };
	}
}
