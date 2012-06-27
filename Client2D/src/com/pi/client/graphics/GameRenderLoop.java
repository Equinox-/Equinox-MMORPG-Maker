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
import com.pi.common.database.TileLayer;
import com.pi.common.database.def.EntityDef;
import com.pi.common.game.Entity;
import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.Renderable;

/**
 * The render loop for the actual game.
 * 
 * @author Westin
 * 
 */
public class GameRenderLoop implements Renderable {
	/**
	 * The client instance this render loop is bound to.
	 */
	private final Client client;
	/**
	 * A temporary field to allow multiple render methods to work together.
	 */
	private IGraphics g;
	/**
	 * The current tile view window.
	 */
	private Rectangle tileView = new Rectangle();
	/**
	 * The x coordinate the current tile view was calculated at.
	 */
	private int currentTileViewX = -1;
	/**
	 * The z coordinate the current tile view was calculated at.
	 */
	private int currentTileViewZ = -1;
	/**
	 * The render distance for entities.
	 */
	private int renderDistance = 0;

	/**
	 * Creates the game render loop for a specified client.
	 * 
	 * @param sClient the bound client
	 */
	public GameRenderLoop(final Client sClient) {
		this.client = sClient;
	}

	@Override
	public final void render(final IGraphics iG) {
		this.g = iG;
		if (client.getWorld() != null) {
			TileLayer t;
			Sector sec = client.getWorld().getSector(0, 0, 0);
			if (client.getEntityManager().getLocalEntity() != null) {
				Entity e =
						client.getEntityManager()
								.getLocalEntity()
								.getWrappedEntity();
				if (e != null) {
					if (client.getEntityManager()
							.getLocalEntity() != null
							&& sec != null) {
						getTileView();
						List<ClientEntity> entities =
								client.getEntityManager()
										.getEntitiesWithin(e,
												renderDistance);
						for (int tI = 0; tI < TileLayer.MAX_VALUE
								.ordinal(); tI++) {
							t = TileLayer.values()[tI];
							renderLayer(t);
							for (ClientEntity ent : entities) {
								if (ent.getWrappedEntity()
										.getLayer() == t) {
									ent.processMovement();
									EntityDef def =
											client.getDefs()
													.getEntityLoader()
													.getDef(ent
															.getWrappedEntity()
															.getEntityDef());
									if (def != null) {
										float frameWidth =
												def.getPositionWidth()
														/ def.getHorizontalFrames();
										float frameHeight =
												def.getPositionHeight() / 4;
										Point p =
												locationToScreen(ent
														.getWrappedEntity());
										g.drawImage(
												def.getGraphic(),
												p.x
														+ ent.getXOff(),
												p.y
														+ ent.getZOff(),
												(int) def
														.getPositionX()
														+ (int) ((int) (ent
																.getMovementPercent() * def
																.getHorizontalFrames()) * frameWidth),
												(int) (def
														.getPositionY() + (frameHeight * ent
														.getWrappedEntity()
														.getDir()
														.ordinal())),
												(int) frameWidth,
												(int) frameHeight);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Renders the specified layer using the current tile view.
	 * 
	 * @param l the tile layer to render
	 */
	private void renderLayer(final TileLayer l) {
		Location tile = new Location(0, 0, 0);
		for (int x = tileView.x; x <= tileView.x
				+ tileView.width; x++) {
			for (int z = tileView.y; z <= tileView.y
					+ tileView.height; z++) {
				tile.setLocation(x, 0, z);
				Sector s =
						client.getWorld().getSector(
								tile.getSectorX(), 0,
								tile.getSectorZ());
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

	/**
	 * Converts the location in the world to a point on the screen.
	 * 
	 * @param t the world location
	 * @return the screen location, or <code>null</code> if unable to calculate.
	 */
	private Point locationToScreen(final Location t) {
		ClientEntity ent =
				client.getEntityManager().getLocalEntity();
		if (ent != null) {
			float xT = t.x - ent.getWrappedEntity().x;
			float zT = t.z - ent.getWrappedEntity().z;
			xT *= TileConstants.TILE_WIDTH;
			zT *= TileConstants.TILE_HEIGHT;
			xT += g.getClip().getCenterX();
			zT += g.getClip().getCenterY();
			return new Point((int) xT - ent.getXOff(), (int) zT
					- ent.getZOff());
		}
		return null;
	}

	/**
	 * Returns the current tile view and recalculates it if the local entity has
	 * moved since the last calculation.
	 * 
	 * @return the current tile view
	 */
	private Rectangle getTileView() {
		Rectangle clip = g.getClip();
		ClientEntity ent =
				client.getEntityManager().getLocalEntity();
		if (ent != null
				&& (currentTileViewX != ent.getWrappedEntity().x || currentTileViewZ != ent
						.getWrappedEntity().z)) {
			int tileWidth =
					(int) Math.ceil(clip.getWidth()
							/ TileConstants.TILE_WIDTH / 2 + 1);
			int tileHeight =
					(int) Math.ceil(clip.getHeight()
							/ TileConstants.TILE_HEIGHT / 2 + 1);
			renderDistance = tileWidth + tileHeight;
			tileView.setBounds(ent.getWrappedEntity().x
					- tileWidth, ent.getWrappedEntity().z
					- tileHeight, tileWidth * 2, tileHeight * 2);
			currentTileViewX = ent.getWrappedEntity().x;
			currentTileViewZ = ent.getWrappedEntity().z;
			return tileView;
		}
		return tileView;
	}
}
