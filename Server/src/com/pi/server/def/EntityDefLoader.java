package com.pi.server.def;

import java.io.File;

import com.pi.common.database.def.EntityDef;
import com.pi.common.database.io.DatabaseIO;
import com.pi.common.net.packet.Packet13EntityDef;
import com.pi.server.Server;
import com.pi.server.database.Paths;

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
	 * @param sServer the controller
	 */
	public EntityDefLoader(final Server sServer) {
		super(sServer);
	}

	@Override
	protected final boolean loadDefinition(final int defID) {
		try {
			File f = Paths.getEntityDef(defID);
			EntityDef d =
					(EntityDef) DatabaseIO.read(f,
							EntityDef.class);
			super.setDef(defID, d);
			return true;
		} catch (Exception e) {
			getServer().getLog().printStackTrace(e);
		}
		return false;
	}

	@Override
	protected final boolean sendDefinitionTo(final int client,
			final int defID, final EntityDef def) {
		Packet13EntityDef packet = new Packet13EntityDef();
		packet.def = def;
		packet.entityID = defID;
		getServer().getClientManager().getClient(client)
				.getNetClient().send(packet);
		return true;
	}
}
