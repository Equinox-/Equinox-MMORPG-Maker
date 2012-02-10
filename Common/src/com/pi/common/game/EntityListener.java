package com.pi.common.game;

import com.pi.common.database.Location;

public interface EntityListener {
    public void entityMove(int entity, Location from, Location to);

    public void entityLayerChange(int entity, int i, int t);

    public void entityCreate(int entity);

    public void entityDispose(int entity);
}
