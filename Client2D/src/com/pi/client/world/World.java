package com.pi.client.world;

import com.pi.client.Client;

public class World {
    private final SectorManager sectorManager;
    private final Client client;
    private final SectorWriter sectorWriter;

    public World(Client client) {
	this.client = client;
	this.sectorManager = new SectorManager(this.client);
	this.sectorWriter = new SectorWriter(this.client);
    }

    public void dispose() {
	sectorManager.dispose();
	sectorWriter.dispose();
    }

    public SectorManager getSectorManager() {
	return sectorManager;
    }

    public SectorWriter getSectorWriter() {
	return sectorWriter;
    }
}
