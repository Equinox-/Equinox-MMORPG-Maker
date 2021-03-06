package com.pi.server.def;

import java.io.File;

import com.pi.common.database.def.entity.EntityDef;
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
	protected final boolean sendDefinitionTo(final int client,
			final int defID, final EntityDef def) {
		Packet13EntityDef packet = new Packet13EntityDef();
		packet.def = def;
		packet.entityID = defID;
		getServer().getClientManager().getClient(client)
				.getNetClient().send(packet);
		return true;
	}

	@Override
	protected final void loadAllDefinitions() {
		File[] defFiles =
				Paths.getEntityDefDirectory().listFiles();
		for (File defF : defFiles) {
			String[] parts = defF.getName().split("\\.");
			if (parts.length == 2 && parts[1].equals("def")) {
				try {
					int defID = Integer.valueOf(parts[0]);
					EntityDef sDef =
							(EntityDef) DatabaseIO.read(defF,
									new EntityDef(defID));
					if (sDef != null) {
						setDef(defID, sDef);
					}
				} catch (NumberFormatException e) {
					continue;
				} catch (Exception e) {
					getServer().getLog().printStackTrace(e);
				}
			}
		}
	}
}
