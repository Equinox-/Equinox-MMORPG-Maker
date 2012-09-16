package com.pi.common.game.entity.comp;

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
public class EntityComponentType {
	private static List<EntityComponentRegistration> componentById = new ArrayList<EntityComponentRegistration>();
	private static Map<Class<? extends EntityComponent>, EntityComponentRegistration> idByComponent = new HashMap<Class<? extends EntityComponent>, EntityComponentRegistration>();

	static {
		// Register components
		registerComponentType(HealthComponent.class);
		registerComponentType(ItemLinkageComponent.class);
	}

	private static void registerComponentType(
			Class<? extends EntityComponent> clazz) {
		EntityComponentRegistration reg = new EntityComponentRegistration(
				componentById.size(), clazz);
		componentById.add(reg);
		idByComponent.put(clazz, reg);
	}

	public static int getComponentID(Class<? extends EntityComponent> clazz) {
		EntityComponentRegistration reg = idByComponent.get(clazz);
		if (reg != null) {
			return reg.ID;
		} else {
			return -1;
		}
	}

	public static Class<? extends EntityComponent> getComponentClass(int id) {
		EntityComponentRegistration reg = componentById.get(id);
		if (reg != null) {
			return reg.clazz;
		} else {
			return null;
		}
	}

	private static class EntityComponentRegistration {
		private int ID;
		private Class<? extends EntityComponent> clazz;

		private EntityComponentRegistration(int ID,
				Class<? extends EntityComponent> clazz) {
			this.ID = ID;
			this.clazz = clazz;
		}
	}
}
