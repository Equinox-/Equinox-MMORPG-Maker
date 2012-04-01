package com.pi.client.graphics;

import java.awt.Point;
import java.awt.Rectangle;

import com.pi.client.Client;
import com.pi.client.graphics.device.IGraphics;
import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.database.Location;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.database.def.EntityDef;
import com.pi.common.game.Entity;

public class GameRenderLoop implements Renderable {
    private final Client client;
    private IGraphics gI;

    public GameRenderLoop(final Client client) {
	this.client = client;
    }

    @Override
    public void render(IGraphics g) {
	gI = g;
	if (client.getWorld() != null) {
	    TileLayer t;
	    Sector sec = client.getWorld().getSectorManager()
		    .getSector(0, 0, 0);
	    if (sec != null)
		for (int tI = 0; tI < TileLayer.MAX_VALUE.ordinal(); tI++) {
		    t = TileLayer.values()[tI];
		    renderSectorLayer(0, 0, sec, g, t);
		    if (client.getEntityManager().getLocalEntity() != null) {
			Entity ent = client.getEntityManager().getLocalEntity();
			if (ent.getLayer() == t) {
			    EntityDef def = client.getDefs().getEntityLoader()
				    .getDef(ent.getEntityDef());
			    if (def != null) {
				float frameWidth = def.getPositionWidth()
					/ def.getHorizontalFrames();
				float frameHeight = def.getPositionHeight() / 4;
				Point p = locationToScreen(ent);
				g.drawImage(def.getGraphic(), p.x, p.y
					+ TileConstants.TILE_HEIGHT
					- (int) frameHeight, (int) def
					.getPositionX(), (int) (def
					.getPositionY() + (frameHeight * ent
					.getDir())), (int) frameWidth,
					(int) frameHeight);
			    }
			}
		    }

		}
	}
    }

    private Point locationToScreen(Location t) {
	Entity ent = client.getEntityManager().getLocalEntity();
	if (ent != null) {
	    float _xT = t.x - ent.x;
	    float _yT = t.z - ent.z;
	    _xT *= TileConstants.TILE_WIDTH;
	    _yT *= TileConstants.TILE_HEIGHT;
	    _xT += gI.getClip().getCenterX() - (TileConstants.TILE_WIDTH / 2);
	    _yT += gI.getClip().getCenterY() - (TileConstants.TILE_HEIGHT / 2);
	    return new Point((int) _xT, (int) _yT);
	}
	return null;
    }

    private void renderSectorLayer(int offX, int offY, Sector sec, IGraphics g,
	    TileLayer t2) {

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
		    if (t != null)
			if (t.getLayer(t2) != null) {
			    g.drawImage(t.getLayer(t2), offX
				    + (x * TileConstants.TILE_WIDTH), offY
				    + (y * TileConstants.TILE_HEIGHT),
				    TileConstants.TILE_WIDTH,
				    TileConstants.TILE_HEIGHT);
			}
		}
	}
    }
}
