package com.pi.server.constants;

import com.pi.common.database.Location;

/**
 * This server's configuration.
 * 
 * @author Westin
 * 
 */
public final class Configuration {
	/**
	 * The spawn location for new clients.
	 */
	public static final Location SPAWN_POINT = new Location(0,
			0, 0);

	/**
	 * Overridden constructor to prevent instances from being created.
	 */
	private Configuration() {

	}
}
