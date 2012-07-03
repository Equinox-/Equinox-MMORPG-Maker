package com.pi.server.database;

import java.io.IOException;

import com.pi.common.contants.SectorConstants;
import com.pi.common.contants.TileConstants;
import com.pi.common.database.GraphicsObject;
import com.pi.common.database.Sector;
import com.pi.common.database.Tile;
import com.pi.common.database.TileGraphicsObject;
import com.pi.common.database.TileLayer;
import com.pi.common.database.def.EntityDef;
import com.pi.common.database.io.DatabaseIO;

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
		sec.setRevision(2);
		sec.setSectorLocation(0, 0, 0);
		for (int x = 0; x < SectorConstants.SECTOR_WIDTH; x++) {
			for (int y = 0; y < SectorConstants.SECTOR_HEIGHT; y++) {
				Tile t = new Tile();
				TileGraphicsObject obj =
						new TileGraphicsObject();
				obj.setGraphic(2);
				obj.setPosition(0, 32, 0, 0);
				t.setLayer(TileLayer.GROUND, obj);
				sec.setLocalTile(x, y, t);
				if (Math.random() < 0.75f) {
					continue;
				}
				TileGraphicsObject obj2 =
						new TileGraphicsObject();
				obj2.setGraphic(2);
				obj2.setPosition(
						32 + (Math.round(Math.random() * 2D) * 32),
						Math.round(Math.random()) * 32, 0, 0);
				t.setLayer(TileLayer.MASK1, obj2);

			}
		}
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 5; y++) {
				TileGraphicsObject obj =
						new TileGraphicsObject();
				obj.setGraphic(2);
				obj.setPosition(x * TileConstants.TILE_WIDTH,
						(5 + y) * TileConstants.TILE_HEIGHT,
						TileConstants.TILE_WIDTH,
						TileConstants.TILE_HEIGHT);
				sec.getLocalTile(5 + x, 5 + y).setLayer(
						TileLayer.FRINGE1, obj);
			}
		}
		return sec;
	}

	/**
	 * Creates and saves the demonstration information.
	 * 
	 * @param args not used
	 * @throws IOException if an error occurs
	 */
	public static void main(final String[] args)
			throws IOException {
		try {
			DatabaseIO.write(Paths.getSectorFile(0, 0, 0),
					create());
		} catch (IOException e) {
			e.printStackTrace();
		}
		EntityDef d = new EntityDef();
		d.setGraphic(3);
		d.setPosition(0, 0, 128, 192);
		// d.setLogicClass("com.pi.server.logic.entity.AggressiveEntityLogic");
		DatabaseIO.write(Paths.getEntityDef(0), d);

		d.setGraphic(4);
		d.setPosition(0, 0, 128, 128);
		d.setLogicClass("com.pi.server.logic.entity.RandomEntityLogic");
		DatabaseIO.write(Paths.getEntityDef(1), d);
	}

	/**
	 * Overridden constructor to prevent instances from being created.
	 */
	private Demonstration() {
	}
}
