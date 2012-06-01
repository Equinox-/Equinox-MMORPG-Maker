package com.pi.client.world;

import com.pi.client.Client;

public class World {
	private final SectorManager sectorManager;
	private final Client client;

	public World(Client client) {
		this.client = client;
		this.sectorManager = new SectorManager(this.client);
	}

	public void dispose() {
		sectorManager.dispose();
	}

	public SectorManager getSectorManager() {
		return sectorManager;
	}
}
