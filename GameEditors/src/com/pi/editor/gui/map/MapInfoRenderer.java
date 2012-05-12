package com.pi.editor.gui.map;

import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.Tile.TileLayer;
import com.pi.graphics.device.IGraphics;

public interface MapInfoRenderer {
    public void renderMapTile(IGraphics g, int baseX, int baseY, int tileX,
	    int tileY, Tile tile);

    public void onMapClick(Sector s, int button, int tileX, int tileY,
	    int internalX, int internalY);

    public void onMapDrag(Sector s, int button, int tileX, int tileY,
	    int internalX, int internalY);
    
    public int[] getCurrentTiledata();
    
    public TileLayer getCurrentTileLayer();
}
