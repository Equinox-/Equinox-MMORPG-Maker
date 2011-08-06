package com.pi.client.graphics;

import java.awt.*;

import com.pi.client.Client;
import com.pi.client.graphics.device.IGraphics;
import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.database.*;
import com.pi.common.database.Tile.TileLayer;

public class GameRenderLoop implements Renderable {
    private final Client client;

    public GameRenderLoop(final Client client) {
	this.client = client;
    }

    @Override
    public void render(IGraphics g) {
	Rectangle clip = g.getClip();
	if (client.getWorld() != null) {
	    Point mySector = client.player.getSector();
	    Sector[][] sec = new Sector[3][3];
	    for (int x = 0; x < 3; x++)
		for (int y = 0; y < 3; y++) {
		    sec[x][y] = client
			    .getWorld()
			    .getSectorManager()
			    .getSector(mySector.x - 1 + x, 0,
				    mySector.y - 1 + y);
		}
	    int sec11X = (int) clip.getCenterX() - client.player.getLocalX()
		    * TileConstants.TILE_WIDTH - (TileConstants.TILE_WIDTH / 2);
	    int sec11Y = (int) clip.getCenterY() - client.player.getLocalY()
		    * TileConstants.TILE_HEIGHT
		    - (TileConstants.TILE_HEIGHT / 2);
	    for (TileLayer l : TileLayer.values()) {
		for (int x = 0; x < 3; x++)
		    for (int y = 0; y < 3; y++) {
			renderSectorLayer(
				sec11X
					- ((x - 1) * (TileConstants.TILE_WIDTH * SectorConstants.SECTOR_WIDTH)),
				sec11Y
					- ((y - 1) * (TileConstants.TILE_HEIGHT * SectorConstants.SECTOR_HEIGHT)),
				sec[x][y], g, l);
		    }
		if (client.player.getLayer().equals(l)) {
		    g.drawImage(
			    "char_1",
			    (int) (clip.getCenterX() - (TileConstants.TILE_WIDTH / 2)),
			    (int) (clip.getCenterY()
				    + (TileConstants.TILE_HEIGHT / 2) - 50), 0,
			    client.player.getDir() * 50, 32, 50);
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
