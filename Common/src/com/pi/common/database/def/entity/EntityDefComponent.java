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
	/**
	 * Creates the default components for an entity created with this definition
	 * component. This can be any number of components, including <code>0</code>
	 * .
	 * 
	 * @return the components to register with the entity
	 */
	public abstract EntityComponent[] createDefaultComponents();
}
