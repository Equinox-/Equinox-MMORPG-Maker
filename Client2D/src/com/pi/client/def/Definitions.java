package com.pi.client.def;

import com.pi.client.Client;
import com.pi.client.ClientThread;

/**
 * The container providing all the definition loaders.
 * 
 * @author Westin
 * @see com.pi.client.def.EntityDefLoader
 */
public class Definitions extends ClientThread {
	/**
	 * The entity definition loader.
	 */
	private final EntityDefLoader entityDefLoader;

	/**
	 * Creates a definitions loader for the specified client, and starts the
	 * thread.
	 * 
	 * @param client the client instance
	 */
	public Definitions(final Client client) {
		super(client);
		this.entityDefLoader = new EntityDefLoader(client);
		super.start();
	}

	/**
	 * Gets the entity definitions loader.
	 * 
	 * @return the entity definitions loader instance
	 * @see com.pi.client.def.EntityDefLoader
	 */
	public final EntityDefLoader getEntityLoader() {
		return entityDefLoader;
	}

	@Override
	protected final void loop() {
		entityDefLoader.loadLoop();
	}
}
