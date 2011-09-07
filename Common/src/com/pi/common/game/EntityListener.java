package com.pi.common.game;

import com.pi.common.database.Location;
import com.pi.common.database.Tile.TileLayer;

public interface EntityListener {
    public void entityMove(int entity, Location from, Location to);

    public void entityLayerChange(int entity, TileLayer from, TileLayer to);

    public void entityCreate(int entity);

    public void entityDispose(int entity);
}
