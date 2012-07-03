package com.pi.server.logic;

import java.util.Iterator;

import com.pi.common.database.def.EntityDef;
import com.pi.server.Server;
import com.pi.server.ServerThread;
import com.pi.server.entity.ServerEntity;
import com.pi.server.logic.entity.EntityLogic;

/**
 * Main thread that processes all artificial intelligence and delayed events.
 * 
 * @author Westin
 * 
 */
public class ServerLogic extends ServerThread {

	/**
	 * Creates a server logic instance for the given server.
	 * 
	 * @param server the server instance
	 */
	public ServerLogic(final Server server) {
		super(server);
	}

	@Override
	public final void loop() {
		Iterator<ServerEntity> itr =
				getServer().getServerEntityManager()
						.getEntities();
		while (itr.hasNext()) {
			doEntityLogic(itr.next());
		}
	}

	/**
	 * Does the entity logic for the given server entity.
	 * 
	 * @param e the server entity
	 */
	public final void doEntityLogic(final ServerEntity e) {
		EntityLogic logic = e.getLogic();
		if (logic == null) {
			logic = loadEntityLogic(e);
		}
		if (logic != null) {
			logic.doLogic();
		}
	}

	/**
	 * Loads the entity logic for the given server entity.
	 * 
	 * @param e the entity to load logic for
	 * @return the entity logic, or <code>null</code> if not loaded
	 */
	public final EntityLogic loadEntityLogic(final ServerEntity e) {
		EntityDef def =
				getServer()
						.getDefs()
						.getEntityLoader()
						.getDef(e.getWrappedEntity()
								.getEntityDef());
		if (def != null && def.getLogicCLass().length() > 0) {
			try {
				Class<?> clazz =
						getContextClassLoader().loadClass(
								def.getLogicCLass());
				EntityLogic l =
						(EntityLogic) clazz
								.getConstructor(
										ServerEntity.class,
										Server.class)
								.newInstance(e, getServer());
				e.assignLogic(l);
				return l;
			} catch (Exception e1) {
				getServer().getLog().printStackTrace(e1);
			}
		}
		return null;
	}
}
