package com.pi.server.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.pi.common.database.Account;
import com.pi.common.database.io.DatabaseIO;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * The database that stores account instances.
 * 
 * @author Westin
 * 
 */
public class AccountDatabase implements PacketObject {
	/**
	 * The list of accounts that this database has registered.
	 */
	private ArrayList<Account> list;

	/**
	 * Creates and loads the accounts database from the file defined by
	 * {@link Paths#getAccountsDatabase()}.
	 * 
	 * @throws IOException if a read error occurs
	 */
	public AccountDatabase() throws IOException {
		list = new ArrayList<Account>();
		if (Paths.getAccountsDatabase().exists()) {
			readData(new PacketInputStream(
					DatabaseIO
							.readByteBuffer(new FileInputStream(
									Paths.getAccountsDatabase()))));
		}
	}

	/**
	 * Gets the account instance bound to the given username.
	 * 
	 * @param username the username to search for
	 * @return the account instance, or <code>null</code> if not found
	 */
	public final Account getAccount(final String username) {
		for (Account acc : list) {
			if (acc.getUsername().equalsIgnoreCase(username)) {
				return acc;
			}
		}
		return null;
	}

	/**
	 * Adds an account with the given username and password hash to the
	 * database.
	 * 
	 * @param username the username
	 * @param passwordHash the password hash
	 * @return <code>true</code> if added, or <code>false</code> if an account
	 *         with the given username is already registered
	 */
	public final boolean addAccount(final String username,
			final String passwordHash) {
		if (getAccount(username) != null) {
			return false;
		}
		Account acc = new Account();
		acc.setPasswordHash(passwordHash);
		acc.setUsername(username);
		list.add(acc);
		return true;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(list.size());
		for (Account a : list) {
			a.writeData(pOut);
		}
	}

	@Override
	public final int getLength() {
		int len = 4;
		for (Account a : list) {
			len += a.getLength();
		}
		return len;
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		list.clear();
		if (pIn.getByteBuffer().remaining() >= 4) {
			int size = pIn.readInt();
			System.out.println(size);
			list.ensureCapacity(size);
			for (int i = 0; i < size; i++) {
				Account a = new Account();
				a.readData(pIn);
				list.add(a);
			}
		}
	}
}
