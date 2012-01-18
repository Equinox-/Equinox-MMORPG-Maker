package com.pi.common.database;

import com.pi.common.contants.GlobalConstants;

public abstract class LivingEntity extends Entity {
	private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private int health;

    public int getHealth() {
	return health;
    }
}
