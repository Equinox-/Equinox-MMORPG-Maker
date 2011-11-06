package com.pi.client.def;

import com.pi.client.Client;

public class Definitions {
    private final Client client;
    private final EntityDefLoader entityDefLoader;

    public Definitions(Client client) {
	this.client = client;
	this.entityDefLoader = new EntityDefLoader(client);
    }

    public EntityDefLoader getEntityLoader() {
	return entityDefLoader;
    }

    public void dispose() {
	//entityDefLoader.dispose();
    }
}
