package com.pi.client.graphics;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.pi.client.Client;
import com.pi.client.entity.ClientEntity;
import com.pi.common.contants.TileConstants;
import com.pi.common.database.GraphicsObject;
import com.pi.common.database.Location;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.Tile.TileLayer;
import com.pi.common.database.def.EntityDef;
import com.pi.common.game.Entity;
import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.Renderable;

public class GameRenderLoop implements Renderable {
	private final Client client;
	private IGraphics g;
	private Rectangle tileView = new Rectangle();
	int tileViewX = -1, tileViewZ = -1;

	public GameRenderLoop(final Client client) {
		this.client = client;
	}

	@Override
	public void render(IGraphics g) {
		this.g = g;
		if (client.getWorld() != null) {
			TileLayer t;
			Sector sec = client.getWorld().getSectorManager()
					.getSector(0, 0, 0);
			Entity e;
			if ((e = client.getEntityManager().getLocalEntity()) != null) {
				List<ClientEntity> entities = client.getEntityManager()
						.getEntitiesWithin(e, 75);
				for (ClientEntity ent : entities)
					ent.processMovement();
				if (client.getEntityManager().getLocalEntity() != null
						&& sec != null) {
					getTileView();
					for (int tI = 0; tI < TileLayer.MAX_VALUE.ordinal(); tI++) {
						t = TileLayer.values()[tI];
						// renderSectorLayer(0, 0, sec, g, t);
						renderLayer(t);
						for (ClientEntity ent : entities) {
							if (ent.getLayer() == t) {
								EntityDef def = client.getDefs()
										.getEntityLoader()
										.getDef(ent.getEntityDef());
								if (def != null) {
									float frameWidth = def.getPositionWidth()
											/ def.getHorizontalFrames();
									float frameHeight = def.getPositionHeight() / 4;
									Point p = locationToScreen(ent);
									g.drawImage(
											def.getGraphic(),
											p.x + ent.getXOff(),
											p.y + ent.getZOff(),
											(int) def.getPositionX()
													+ (int) ((int) (ent
															.getMovementPercent() * def
															.getHorizontalFrames()) * frameWidth),
											(int) (def.getPositionY() + (frameHeight * ent
													.getDir().ordinal())),
											(int) frameWidth, (int) frameHeight);
								}
							}
						}
					}
				}
			}
		}
	}

	private void renderLayer(TileLayer l) {
		Location tile = new Location(0, 0, 0);
		for (int x = tileView.x; x <= tileView.x + tileView.width; x++) {
			for (int z = tileView.y; z <= tileView.y + tileView.height; z++) {
				tile.setLocation(x, 0, z);
				Sector s = client.getWorld().getSectorManager()
						.getSector(tile.getSectorX(), 0, tile.getSectorZ());
				Point screen = locationToScreen(tile);
				if (s != null) {
					Tile lTile = s.getGlobalTile(x, z);
					if (lTile != null) {
						GraphicsObject gO = lTile.getLayer(l);
						if (gO != null) {
							g.drawImage(gO, screen.x, screen.y);
						}
					}
				}
			}
		}
	}

	private Point locationToScreen(Location t) {
		ClientEntity ent = client.getEntityManager().getLocalEntity();
		if (ent != null) {
			float _xT = t.x - ent.x;
			float _zT = t.z - ent.z;
			_xT *= TileConstants.TILE_WIDTH;
			_zT *= TileConstants.TILE_HEIGHT;
			_xT += g.getClip().getCenterX();
			_zT += g.getClip().getCenterY();
			return new Point((int) _xT - ent.getXOff(), (int) _zT
					- ent.getZOff());
		}
		return null;
	}

	private Rectangle getTileView() {
		Rectangle clip = g.getClip();
		ClientEntity ent = client.getEntityManager().getLocalEntity();
		if (ent != null && (tileViewX != ent.x || tileViewZ != ent.z)) {
			int tileWidth = (int) Math.ceil(clip.getWidth()
					/ TileConstants.TILE_WIDTH / 2 + 1);
			int tileHeight = (int) Math.ceil(clip.getHeight()
					/ TileConstants.TILE_HEIGHT / 2 + 1);
			tileView.setBounds(ent.x - tileWidth, ent.z - tileHeight,
					tileWidth * 2, tileHeight * 2);
			tileViewX = ent.x;
			tileViewZ = ent.z;
			return tileView;
		}
		return tileView;
	}

	/*
	 * private void renderSectorLayer(int offX, int offY, Sector sec, IGraphics
	 * g, TileLayer t2) { if (sec != null) { Rectangle clip = g.getClip(); int
	 * tileClipWidth = (int) Math.abs(Math.ceil(clip.width /
	 * TileConstants.TILE_WIDTH) - Math.floor(offX / TileConstants.TILE_WIDTH))
	 * + 1; int tileClipHeight = (int) Math.abs(Math.ceil(clip.height /
	 * TileConstants.TILE_HEIGHT) - Math.floor(offY /
	 * TileConstants.TILE_HEIGHT)) + 1; int startX = 0; int startY = 0; if (offX
	 * < 0) { startX = offX / -TileConstants.TILE_WIDTH; } if (offY < 0) {
	 * startY = offY / -TileConstants.TILE_HEIGHT; } int endX =
	 * Math.min(SectorConstants.SECTOR_WIDTH, tileClipWidth); int endY =
	 * Math.min(SectorConstants.SECTOR_HEIGHT, tileClipHeight);
	 * 
	 * for (int x = startX; x <= endX; x++) for (int y = startY; y <= endY; y++)
	 * { Tile t = sec.getLocalTile(x, y); if (t != null) if (t.getLayer(t2) !=
	 * null) { g.drawImage(t.getLayer(t2), offX + (x *
	 * TileConstants.TILE_WIDTH), offY + (y * TileConstants.TILE_HEIGHT),
	 * TileConstants.TILE_WIDTH, TileConstants.TILE_HEIGHT); } } } }
	 */
}
