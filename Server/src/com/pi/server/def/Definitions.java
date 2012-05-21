package com.pi.server.def;

import com.pi.server.Server;

public class Definitions {
	// private final Server server;
	private final EntityDefLoader entityDefLoader;

	public Definitions(Server server) {
		// this.server = server;
		this.entityDefLoader = new EntityDefLoader(server);
	}

	public EntityDefLoader getEntityLoader() {
		return entityDefLoader;
	}

	public void dispose() {
		entityDefLoader.dispose();
	}
}
