package com.pi.client.graphics;

import com.pi.client.Client;
import com.pi.client.graphics.device.IGraphics;
import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;

public class GameRenderLoop implements Renderable {
    private final Client client;

    public GameRenderLoop(final Client client) {
	this.client = client;
    }

    @Override
    public void render(IGraphics g) {
	if (client.getWorld() != null) {
	    Sector sec = client.getWorld().getSectorManager().getSector(0, 0);
	    if (sec != null) {
		for (int x = 0; x < SectorConstants.SECTOR_WIDTH; x++)
		    for (int y = 0; y < SectorConstants.SECTOR_HEIGHT; y++) {
			Tile t = sec.getLocalTile(x, y);
			if (t.getGround() != null)
			    g.drawImage(t.getGround(), x
				    * TileConstants.TILE_WIDTH, y
				    * TileConstants.TILE_HEIGHT);
			if (t.getMask1() != null)
			    g.drawImage(t.getMask1(), x
				    * TileConstants.TILE_WIDTH, y
				    * TileConstants.TILE_HEIGHT);
		    }
	    }
	}
    }
}
