package com.pi.editor.gui.map;

import com.pi.common.database.world.Sector;
import com.pi.common.database.world.Tile;
import com.pi.common.database.world.TileLayer;
import com.pi.graphics.device.IGraphics;

public interface MapInfoRenderer {
	void renderMapTile(IGraphics g, int baseX, int baseY,
			int tileX, int tileY, Tile tile);

	void onMapClick(Sector s, int button, int tileX,
			int tileY, int internalX, int internalY);

	void onMapDrag(Sector s, int button, int tileX,
			int tileY, int internalX, int internalY);

	int[] getCurrentTiledata();

	TileLayer getCurrentTileLayer();
}
