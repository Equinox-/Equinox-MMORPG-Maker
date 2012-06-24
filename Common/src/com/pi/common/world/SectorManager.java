package com.pi.common.world;

import java.util.Map;

import com.pi.common.database.Sector;
import com.pi.common.database.SectorLocation;

public interface SectorManager {
	public Sector getSector(int x, int y, int z);

	public boolean isEmptySector(int x, int y, int z);

	public SectorStorage getSectorStorage(int x, int y, int z);

	public Map<SectorLocation, SectorStorage> loadedMap();

	public static class SectorStorage {
		public long lastUsed;
		public Sector data;
		public boolean empty;
		public boolean requested = false;
	}
}
