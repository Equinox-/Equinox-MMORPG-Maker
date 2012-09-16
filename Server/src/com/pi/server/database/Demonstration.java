package com.pi.server.database;

import java.io.IOException;

import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.contants.TileFlags;
import com.pi.common.database.def.ItemDef;
import com.pi.common.database.def.entity.EntityDef;
import com.pi.common.database.def.entity.HealthDefComponent;
import com.pi.common.database.def.entity.LogicDefComponent;
import com.pi.common.database.io.DatabaseIO;
import com.pi.common.database.io.GraphicsDirectories;
import com.pi.common.database.world.Sector;
import com.pi.common.database.world.Tile;
import com.pi.common.database.world.TileGraphicsObject;
import com.pi.common.database.world.TileLayer;

/**
 * Creates and saves demonstration information to the server's database.
 * 
 * @author Westin
 * 
 */
public final class Demonstration {
	/**
	 * Creates a basic demonstration sector.
	 * 
	 * @return the sector instance
	 */
	public static Sector create() {
		Sector sec = new Sector();
		sec.setRevision(4);
		sec.setSectorLocation(0, 0, 0);
		for (int x = 0; x < SectorConstants.SECTOR_WIDTH; x++) {
			for (int y = 0; y < SectorConstants.SECTOR_HEIGHT; y++) {
				Tile t = new Tile();
				TileGraphicsObject obj = new TileGraphicsObject();
				obj.setGraphic(GraphicsDirectories.TILES.getFileID(1));
				obj.setPosition(0, TileConstants.TILE_HEIGHT, 0, 0);
				t.setLayer(TileLayer.GROUND, obj);
				sec.setLocalTile(x, y, t);
				if (Math.random() < 0.75f) {
					continue;
				}
				TileGraphicsObject obj2 = new TileGraphicsObject();
				obj2.setGraphic(GraphicsDirectories.TILES.getFileID(1));
				obj2.setPosition(
						TileConstants.TILE_WIDTH
								+ (Math.round(Math.random() * 2D) * TileConstants.TILE_WIDTH),
						Math.round(Math.random()) * TileConstants.TILE_HEIGHT,
						0, 0);
				t.setLayer(TileLayer.MASK1, obj2);

			}
		}
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 5; y++) {
				TileGraphicsObject obj = new TileGraphicsObject();
				obj.setGraphic(GraphicsDirectories.TILES.getFileID(1));
				obj.setPosition(x * TileConstants.TILE_WIDTH, (5 + y)
						* TileConstants.TILE_HEIGHT, TileConstants.TILE_WIDTH,
						TileConstants.TILE_HEIGHT);
				sec.getLocalTile(5 + x, 5 + y).setLayer(
						y == 4 ? TileLayer.MASK1 : TileLayer.FRINGE1, obj);
				if (y == 4) {
					sec.getLocalTile(5 + x - 1, 5 + y).applyFlag(
							TileFlags.WALL_EAST);
					sec.getLocalTile(5 + x + 1, 5 + y).applyFlag(
							TileFlags.WALL_WEST);
					sec.getLocalTile(5 + x, 5 + y - 1).applyFlag(
							TileFlags.WALL_SOUTH);
					sec.getLocalTile(5 + x, 5 + y + 1).applyFlag(
							TileFlags.WALL_NORTH);
				}
			}
		}
		return sec;
	}

	/**
	 * Creates and saves the demonstration information.
	 * 
	 * @param args
	 *            not used
	 * @throws IOException
	 *             if an error occurs
	 */
	public static void main(final String[] args) throws IOException {
		try {
			DatabaseIO.write(Paths.getSectorFile(0, 0, 0), create());
		} catch (IOException e) {
			e.printStackTrace();
		}
		EntityDef d0 = new EntityDef(0);
		d0.setGraphic(GraphicsDirectories.ENTITIES.getFileID(1));
		d0.setPosition(0, 0, 128, 192);
		d0.addEntityDefComponent(new HealthDefComponent(10));
		DatabaseIO.write(Paths.getEntityDef(0), d0);

		EntityDef d1 = new EntityDef(1);
		d1.setGraphic(GraphicsDirectories.ENTITIES.getFileID(2));
		d1.setPosition(0, 0, 128, 128);
		d1.addEntityDefComponent(new HealthDefComponent(10));
		d1.addEntityDefComponent(new LogicDefComponent(
				"com.pi.server.logic.entity.NeutralEntityLogic"));
		DatabaseIO.write(Paths.getEntityDef(1), d1);

		ItemDef iDef = new ItemDef(0);
		iDef.setGraphic(GraphicsDirectories.ITEMS.getFileID(1));
		iDef.setPosition(0, 0, 32, 32);
		DatabaseIO.write(Paths.getItemDef(0), iDef);
	}

	/**
	 * Overridden constructor to prevent instances from being created.
	 */
	private Demonstration() {
	}
}
