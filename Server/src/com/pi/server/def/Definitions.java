package com.pi.server.def;

import com.pi.server.Server;
import com.pi.server.ServerThread;

/**
 * The container providing all the definition loaders.
 * 
 * @author Westin
 * @see com.pi.server.def.EntityDefLoader
 */
public class Definitions extends ServerThread {
	/**
	 * The entity definition loader.
	 */
	private final EntityDefLoader entityDefLoader;

	/**
	 * Creates a definitions loader for the specified server, and starts the
	 * thread.
	 * 
	 * @param server the server instance
	 */
	public Definitions(final Server server) {
		super(server);
		this.entityDefLoader = new EntityDefLoader(server);
		super.start();
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

	@Override
	protected final void loop() {
		entityDefLoader.loadLoop();
	}
}
