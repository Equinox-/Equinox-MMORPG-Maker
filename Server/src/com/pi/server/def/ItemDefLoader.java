package com.pi.server.def;

import java.io.File;

import com.pi.common.database.def.ItemDef;
import com.pi.common.database.io.DatabaseIO;
import com.pi.common.net.packet.Packet23ItemDef;
import com.pi.server.Server;
import com.pi.server.database.Paths;

/**
 * The definitions loader for item definitions.
 * 
 * @author mark
 * 
 */
public class ItemDefLoader extends DefinitionsLoader<ItemDef> {
	/**
	 * Creates an item definitions loader with the specified client as the
	 * controller.
	 * 
	 * @param sServer the controller
	 */
	public ItemDefLoader(final Server sServer) {
		super(sServer);
	}

	@Override
	protected final boolean sendDefinitionTo(final int client,
			final int defID, final ItemDef def) {
		Packet23ItemDef packet = new Packet23ItemDef();
		packet.def = def;
		packet.itemID = defID;
		getServer().getClientManager().getClient(client)
				.getNetClient().send(packet);
		return true;
	}

	@Override
	protected final void loadAllDefinitions() {
		File[] defFiles =
				Paths.getItemDefDirectory().listFiles();
		for (File defF : defFiles) {
			String[] parts = defF.getName().split("\\.");
			if (parts.length == 2 && parts[1].equals("def")) {
				try {
					int defID = Integer.valueOf(parts[0]);
					ItemDef sDef =
							(ItemDef) DatabaseIO.read(defF,
									new ItemDef(defID));
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
