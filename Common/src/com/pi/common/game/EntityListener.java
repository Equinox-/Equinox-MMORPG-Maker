package com.pi.common.game;

import com.pi.common.contants.Direction;
import com.pi.common.database.Location;
import com.pi.common.database.Tile.TileLayer;

public interface EntityListener {
	public void entityTeleport(int entity, Location from, Location to);

	public void entityMove(int entity, Location from, Location to, Direction dir);

	public void entityLayerChange(int entity, TileLayer tileLayer, TileLayer t);

	public void entityCreate(int entity);

	public void entityDispose(int entity);
}
