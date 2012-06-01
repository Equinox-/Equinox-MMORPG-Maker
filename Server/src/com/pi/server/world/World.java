package com.pi.server.world;

import com.pi.server.Server;

public class World {
	private final SectorManager sectorManager;
	private final Server server;

	public World(Server server) {
		this.server = server;
		this.sectorManager = new SectorManager(this.server);
	}

	public void dispose() {
		sectorManager.dispose();
	}

	public SectorManager getSectorManager() {
		return sectorManager;
	}
}
