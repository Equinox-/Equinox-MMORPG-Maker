package com.pi.common.database.def.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class that any entity component must be registered with.
 * 
 * @author Westin
 * 
 */
public class EntityDefComponentType {
	private static List<EntityDefComponentRegistration> componentById = new ArrayList<EntityDefComponentRegistration>();
	private static Map<Class<? extends EntityDefComponent>, EntityDefComponentRegistration> idByComponent = new HashMap<Class<? extends EntityDefComponent>, EntityDefComponentRegistration>();

	static {
		// Register components
		registerComponentType(HealthDefComponent.class);
		registerComponentType(LogicDefComponent.class);
	}

	private static void registerComponentType(
			Class<? extends EntityDefComponent> clazz) {
		EntityDefComponentRegistration reg = new EntityDefComponentRegistration(
				componentById.size(), clazz);
		componentById.add(reg);
		idByComponent.put(clazz, reg);
	}

	public static int getComponentID(Class<? extends EntityDefComponent> clazz) {
		EntityDefComponentRegistration reg = idByComponent.get(clazz);
		if (reg != null) {
			return reg.ID;
		} else {
			return -1;
		}
	}

	public static Class<? extends EntityDefComponent> getComponentClass(int id) {
		EntityDefComponentRegistration reg = componentById.get(id);
		if (reg != null) {
			return reg.clazz;
		} else {
			return null;
		}
	}

	private static class EntityDefComponentRegistration {
		private int ID;
		private Class<? extends EntityDefComponent> clazz;

		private EntityDefComponentRegistration(int ID,
				Class<? extends EntityDefComponent> clazz) {
			this.ID = ID;
			this.clazz = clazz;
		}
	}

	public static int getComponentCount() {
		return componentById.size();
	}
}
