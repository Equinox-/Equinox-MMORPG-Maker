package com.pi.server.logic;

import java.util.Iterator;

import com.pi.common.database.def.EntityDef;
import com.pi.server.Server;
import com.pi.server.ServerThread;
import com.pi.server.entity.ServerEntity;
import com.pi.server.logic.entity.EntityLogic;

public class ServerLogic extends ServerThread {

	public ServerLogic(Server server) {
		super(server);
	}

	@Override
	public void loop() {
		Iterator<ServerEntity> itr = server.getServerEntityManager()
				.getEntities();
		while (itr.hasNext()) {
			doEntityLogic(itr.next());
		}
	}

	public void doEntityLogic(ServerEntity e) {
		EntityLogic logic;
		if ((logic = e.getLogic()) == null) {
			logic = loadEntityLogic(e);
		}
		if (logic != null) {
			logic.doLogic();
		}
	}

	public EntityLogic loadEntityLogic(ServerEntity e) {
		EntityDef def = server.getDefs().getEntityLoader()
				.getDef(e.getEntityDef());
		if (def != null && def.getLogicCLass().length() > 0) {
			try {
				Class<?> clazz = getContextClassLoader().loadClass(
						def.getLogicCLass());
				EntityLogic l = (EntityLogic) clazz.getConstructor(
						ServerEntity.class, Server.class)
						.newInstance(e, server);
				e.assignLogic(l);
				return l;
			} catch (Exception e1) {
				server.getLog().printStackTrace(e1);
			}
		}
		return null;
	}
}
