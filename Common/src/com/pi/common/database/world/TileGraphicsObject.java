package com.pi.common.database.world;

import com.pi.common.constants.TileConstants;
import com.pi.common.constants.NetworkConstants.SizeOf;
import com.pi.common.database.GraphicsObject;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * A graphics object with fewer variables so the sectors can be transfered
 * faster.
 * 
 * @author Westin
 * 
 */
public class TileGraphicsObject extends GraphicsObject {

	@Override
	public final float getPositionWidth() {
		return TileConstants.TILE_WIDTH;
	}

	@Override
	public final float getPositionHeight() {
		return TileConstants.TILE_HEIGHT;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut) {
		pOut.writeInt(getGraphic());
		pOut.writeFloat(getPositionX());
		pOut.writeFloat(getPositionY());
	}

	@Override
	public final void readData(final PacketInputStream pIn) {
		setGraphic(pIn.readInt());
		setPosition(pIn.readFloat(), pIn.readFloat(), 1, 1);
	}

	@Override
	public final int getLength() {
		return SizeOf.INT + (2 * SizeOf.FLOAT);
	}
}
