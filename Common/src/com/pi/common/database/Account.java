package com.pi.common.database;

import java.io.IOException;

import com.pi.common.PICryptUtils;
import com.pi.common.contants.ItemConstants;
import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * A class describing an account, for storage in a database.
 * 
 * @author Westin
 * 
 */
public class Account implements PacketObject {
	/**
	 * This account's username.
	 */
	private String username;
	/**
	 * The default hash of this user's password.
	 */
	private String passwordHash;
	/**
	 * The entity definition id.
	 */
	private int entityDef;

	/**
	 * This account's current inventory.
	 */
	private final Inventory inventory = new Inventory(
			ItemConstants.PLAYER_INVENTORY_SIZE);

	/**
	 * The current entity location.
	 */
	private Location entityLocation = new Location();

	/**
	 * Sets the username of this account.
	 * 
	 * @param sUsername the new username.
	 */
	public final void setUsername(final String sUsername) {
		this.username = sUsername;
	}

	/**
	 * Sets the password of this account.
	 * 
	 * @param password the unencrypted password.
	 */
	public final void setPassword(final String password) {
		this.passwordHash = PICryptUtils.crypt(password);
	}

	/**
	 * Sets the raw password hash of this account.
	 * 
	 * @param sPasswordHash the encrypted password
	 */
	public final void setPasswordHash(final String sPasswordHash) {
		this.passwordHash = sPasswordHash;
	}

	/**
	 * Sets the entity definition of this account.
	 * 
	 * @param def the definition id
	 */
	public final void setEntityDef(final int def) {
		this.entityDef = def;
	}

	/**
	 * Gets this account's entity definition id.
	 * 
	 * @return the entity definition id
	 */
	public final int getEntityDef() {
		return this.entityDef;
	}

	/**
	 * Gets this account's username.
	 * 
	 * @return the username
	 */
	public final String getUsername() {
		return username;
	}

	/**
	 * Gets this account's password hash.
	 * 
	 * @return the password hash
	 */
	public final String getPasswordHash() {
		return passwordHash;
	}

	/**
	 * Sets the location of this account.
	 * 
	 * @param x the new x position
	 * @param plane the new plane
	 * @param z the new z position
	 */
	public final void setLocation(final int x, final int plane,
			final int z) {
		entityLocation.setLocation(x, plane, z);
	}

	/**
	 * Sets the entity location to the given location.
	 * 
	 * @param l the new location
	 */
	public final void setLocation(final Location l) {
		entityLocation = l;
	}

	/**
	 * Gets the location of this account.
	 * 
	 * @return the entity location
	 */
	public final Location getLocation() {
		return entityLocation;
	}

	/**
	 * Gets this account's inventory.
	 * 
	 * @return the inventory
	 */
	public final Inventory getInventory() {
		return inventory;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(entityDef);
		pOut.writeString(username);
		pOut.writeString(passwordHash);
		entityLocation.writeData(pOut);
		inventory.writeData(pOut);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		entityDef = pIn.readInt();
		username = pIn.readString();
		passwordHash = pIn.readString();
		entityLocation.readData(pIn);
		inventory.readData(pIn);
	}

	@Override
	public final int getLength() {
		return SizeOf.INT
				+ PacketOutputStream.stringByteLength(username)
				+ PacketOutputStream
						.stringByteLength(passwordHash)
				+ entityLocation.getLength()
				+ inventory.getLength();
	}

	@Override
	public final String toString() {
		return username;
	}
}
