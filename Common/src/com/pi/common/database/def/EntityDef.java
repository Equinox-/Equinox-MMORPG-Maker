package com.pi.common.database.def;

import com.pi.common.contants.GlobalConstants;
import com.pi.common.database.GraphicsObject;

public class EntityDef extends GraphicsObject {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private int defID;

    public int getDefID() {
	return defID;
    }
}
