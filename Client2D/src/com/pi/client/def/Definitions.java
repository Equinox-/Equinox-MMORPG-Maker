package com.pi.client.def;

import com.pi.client.Client;
import com.pi.client.ClientThread;

public class Definitions extends ClientThread{
    private final EntityDefLoader entityDefLoader;

    public Definitions(Client client) {
	super(client);
	this.entityDefLoader = new EntityDefLoader(client);
	super.start();
    }

    public EntityDefLoader getEntityLoader() {
	return entityDefLoader;
    }

    @Override
    protected void loop() {
	entityDefLoader.loadLoop();
    }
}
