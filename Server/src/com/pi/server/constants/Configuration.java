package com.pi.server.constants;

import com.pi.common.database.Location;

public class Configuration {
	public static Location spawn_point;
	static {
		spawn_point = new Location(0, 0, 0);
	}
}
