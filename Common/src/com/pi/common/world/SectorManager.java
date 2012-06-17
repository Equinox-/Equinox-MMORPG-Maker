package com.pi.common.world;

import com.pi.common.database.Sector;

public interface SectorManager {
	public Sector getSector(int x, int y, int z);

	public boolean isEmptySector(int x, int y, int z);
}
