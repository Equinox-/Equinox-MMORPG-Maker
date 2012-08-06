package com.pi.client.def;

import com.pi.client.Client;
import com.pi.common.database.def.ItemDef;
import com.pi.common.net.packet.Packet22ItemDefRequest;

/**
 * The definitions loader for item definitions.
 * 
 * @author Westin
 * 
 */
public class ItemDefLoader extends DefinitionsLoader<ItemDef> {

	/**
	 * Creates an item definitions loader with the specified client as the
	 * controller.
	 * 
	 * @param sClient the controller
	 */
	public ItemDefLoader(final Client sClient) {
		super(sClient);
	}

	@Override
	protected final boolean requestDefinition(final int defID) {
		getClient().getNetwork().send(
				Packet22ItemDefRequest.create(defID));
		return true;
	}
}
