package com.pi.client.def;

import com.pi.client.Client;
import com.pi.common.database.def.entity.EntityDef;
import com.pi.common.net.packet.Packet12EntityDefRequest;

/**
 * The definitions loader for entity definitions.
 * 
 * @author Westin
 * 
 */
public class EntityDefLoader extends
		DefinitionsLoader<EntityDef> {

	/**
	 * Creates an entity definitions loader with the specified client as the
	 * controller.
	 * 
	 * @param sClient the controller
	 */
	public EntityDefLoader(final Client sClient) {
		super(sClient);
	}

	@Override
	protected final boolean requestDefinition(final int defID) {
		getClient().getNetwork().send(
				Packet12EntityDefRequest.create(defID));
		return true;
	}
}
