package com.pi.common.database.def.entity;

import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.common.net.packet.PacketObject;

/**
 * Extra data for an entity definition.
 * 
 * @author westin
 * 
 */
public abstract class EntityDefComponent implements PacketObject {
	public abstract EntityComponent createDefaultComponent();
}
