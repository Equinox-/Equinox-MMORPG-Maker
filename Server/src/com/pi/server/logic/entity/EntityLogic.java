package com.pi.server.logic.entity;

import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

public interface EntityLogic {
	public void doLogic(Server server, ServerEntity ent);
}
