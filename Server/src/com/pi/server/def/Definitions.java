package com.pi.server.def;

import com.pi.server.Server;

/**
 * The container providing all the definition loaders.
 * 
 * @author Westin
 * @see com.pi.server.def.EntityDefLoader
 */
public class Definitions {
	/**
	 * The entity definition loader.
	 */
	private final EntityDefLoader entityDefLoader;
	/**
	 * The item definition loader.
	 */
	private final ItemDefLoader itemDefLoader;

	/**
	 * Creates a definitions loader for the specified server, and starts the
	 * thread.
	 * 
	 * @param server the server instance
	 */
	public Definitions(final Server server) {
		this.entityDefLoader = new EntityDefLoader(server);
		this.entityDefLoader.loadAllDefinitions();
		this.itemDefLoader = new ItemDefLoader(server);
		this.itemDefLoader.loadAllDefinitions();
	}

	/**
	 * Gets the entity definitions loader.
	 * 
	 * @return the entity definitions loader instance
	 * @see com.pi.server.def.EntityDefLoader
	 */
	public final EntityDefLoader getEntityLoader() {
		return entityDefLoader;
	}

	/**
	 * Gets the item definitions loader.
	 * 
	 * @return the item definitions loader instance
	 * @see com.pi.server.def.EntityDefLoader
	 */
	public final ItemDefLoader getItemLoader() {
		return itemDefLoader;
	}
}
