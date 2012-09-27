package com.pi.common.game.entity.comp;

import java.io.IOException;

import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * An entity component representing the current amount of health an entity has.
 * 
 * @author westin
 * 
 */
public class HealthComponent extends EntityComponent {
	/**
	 * Current health.
	 */
	private int health = 1;

	/**
	 * Creates an empty health component.
	 */
	public HealthComponent() {
	}

	/**
	 * Creates a health component with the given current health.
	 * 
	 * @param sHealth the current health
	 */
	public HealthComponent(final int sHealth) {
		this.health = sHealth;
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
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(health);
	}

	@Override
	public final int getLength() {
		return SizeOf.INT;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		health = pIn.readInt();
	}

	@Override
	public final String toString() {
		return "Health[" + health + "]";
	}
}
