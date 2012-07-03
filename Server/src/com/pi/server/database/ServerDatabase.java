package com.pi.server.database;

import java.io.IOException;

import com.pi.common.database.io.DatabaseIO;
import com.pi.server.Server;

/**
 * Utility class managing all the different databases used by the server.
 * 
 * @see AccountDatabase
 * @author Westin
 * 
 */
public class ServerDatabase {
	/**
	 * The server instance.
	 */
	private final Server server;
	/**
	 * The accounts database.
	 */
	private AccountDatabase accountsDB;

	/**
	 * Creates and loads all the databases to be used with the given server.
	 * 
	 * @param sServer the server database
	 */
	public ServerDatabase(final Server sServer) {
		this.server = sServer;
		try {
			this.accountsDB = new AccountDatabase();
		} catch (IOException e) {
			server.getLog().printStackTrace(e);
			this.accountsDB = null;
		}
	}

	/**
	 * Gets the account database.
	 * 
	 * @return the account database
	 */
	public final AccountDatabase getAccounts() {
		return this.accountsDB;
	}

	/**
	 * Saves all the databases.
	 */
	public final void save() {
		try {
			DatabaseIO.write(Paths.getAccountsDatabase(),
					accountsDB);
		} catch (IOException e) {
			server.getLog().printStackTrace(e);
		}
	}
}
