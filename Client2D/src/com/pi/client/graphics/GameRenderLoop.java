package com.pi.client.graphics;

import java.awt.Rectangle;

import com.pi.client.Client;
import com.pi.client.graphics.device.IGraphics;
import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.database.def.EntityDef;
import com.pi.common.game.Entity;

public class GameRenderLoop implements Renderable {
    private final Client client;

    public GameRenderLoop(final Client client) {
	this.client = client;
    }

    @Override
    public void render(IGraphics g) {
	Rectangle clip = g.getClip();
	if (client.getWorld() != null) {
	    /*
	     * int myPlane =
	     * client.getEntityManager().getLocalEntity().getPlane(); for (int
	     * plane = myPlane; plane >= 0; plane--) {
	     * renderSectorSurround(clip, g, client, plane); }
	     */
	    renderSectorLayer(0, 0, client.getWorld().getSectorManager()
		    .getSector(0, 0, 0), g, TileLayer.values());
	    if (client.getEntityManager().getLocalEntity() != null) {
		Entity ent = client.getEntityManager().getLocalEntity();
		EntityDef def = client.getDefs().getEntityLoader()
			.getDef(ent.getEntityDef());
		if (def != null) {
		    g.drawImage(def, (int) (ent.x * TileConstants.TILE_WIDTH),
			    (int) (ent.z * TileConstants.TILE_HEIGHT));
		}
	    }
	}
    }

    private static void renderSectorLayer(int offX, int offY, Sector sec,
	    IGraphics g, TileLayer... layers) {
	if (layers == null)
	    layers = TileLayer.values();
	if (sec != null) {
	    Rectangle clip = g.getClip();
	    int tileClipWidth = (int) Math.abs(Math.ceil(clip.width
		    / TileConstants.TILE_WIDTH)
		    - Math.floor(offX / TileConstants.TILE_WIDTH)) + 1;
	    int tileClipHeight = (int) Math.abs(Math.ceil(clip.height
		    / TileConstants.TILE_HEIGHT)
		    - Math.floor(offY / TileConstants.TILE_HEIGHT)) + 1;
	    int startX = 0;
	    int startY = 0;
	    if (offX < 0) {
		startX = offX / -TileConstants.TILE_WIDTH;
	    }
	    if (offY < 0) {
		startY = offY / -TileConstants.TILE_HEIGHT;
	    }
	    int endX = Math.min(SectorConstants.SECTOR_WIDTH, tileClipWidth);
	    int endY = Math.min(SectorConstants.SECTOR_HEIGHT, tileClipHeight);

	    for (int x = startX; x <= endX; x++)
		for (int y = startY; y <= endY; y++) {
		    Tile t = sec.getLocalTile(x, y);
		    for (TileLayer layer : layers)
			if (t.getLayer(layer) != null)
			    g.drawImage(t.getLayer(layer), offX
				    + (x * TileConstants.TILE_WIDTH), offY
				    + (y * TileConstants.TILE_HEIGHT),
				    TileConstants.TILE_WIDTH,
				    TileConstants.TILE_HEIGHT);
		}
	}
    }
}
