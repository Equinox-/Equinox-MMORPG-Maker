package com.pi.server.constants;

import com.pi.common.database.Location;

/**
 * Constant values used in other classes.
 * 
 * @author Westin
 * 
 */
public final class ServerConstants {
	/**
	 * The spawn location for new clients.
	 */
	public static final Location SPAWN_POINT = new Location(0,
			0, 0);
	/**
	 * The tile distance from a client for an entity to be disposed.
	 */
	public static final int ENTITY_DISPOSE_DIST = 45;
	/**
	 * The maximum number of clients connected to this server at one time.
	 */
	public static final int MAX_CLIENTS = 10;
	/**
	 * The tile distance from a client for an entity to be updated.
	 */
	public static final int ENTITY_UPDATE_DIST = 50;
	/**
	 * The network port this server operates on.
	 */
	public static final int NETWORK_PORT = 9999;

	/**
	 * The amount of time in milliseconds for an entity to ignore an entity that
	 * attacked it.
	 */
	public static final long ENTITY_ATTACKER_TOLERANCE = 300000;

	/**
	 * Overridden constructor to prevent instances from being produced.
	 */
	private ServerConstants() {

	}
}
