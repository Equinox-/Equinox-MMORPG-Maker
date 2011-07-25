package com.pi.server.world;

import com.pi.server.Server;

public class World {
    private final SectorManager sectorManager;
    private final Server server;
    private final SectorWriter sectorWriter;

    public World(Server server) {
	this.server = server;
	this.sectorManager = new SectorManager(this.server);
	this.sectorWriter = new SectorWriter(this.server);
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
