package com.pi.server.constants;

import com.pi.common.database.EntityDef;
import com.pi.common.database.Location;

public class Configuration {
    public static Location spawn_point;
    public static EntityDef spawn_def;
    static {
	spawn_point = new Location(0, 0, 0);
	spawn_def = new EntityDef();
	spawn_def.setLocation(spawn_point.getGlobalX(), spawn_point.getPlane(),
		spawn_point.getGlobalZ());
    }
}
