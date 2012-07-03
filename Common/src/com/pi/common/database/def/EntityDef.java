package com.pi.common.database.def;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.database.GraphicsObject;
import com.pi.common.game.EntityType;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * The class representing an entity's definition.
 * 
 * @author Westin
 * 
 */
public class EntityDef extends GraphicsObject {
	/**
	 * This definintion's identification number.
	 */
	private int defID;
	/**
	 * The number of horizontal frames for the movement animation.
	 */
	private int horizFrames = 4;
	/**
	 * The logic class name.
	 */
	private String logicClass;

	/**
	 * Gets the number of horizontal frames for the movement animation.
	 * 
	 * @return the horizontal frame count
	 */
	public final int getHorizontalFrames() {
		return horizFrames;
	}

	/**
	 * Sets the number of horizontal frames for the movement animation.
	 * 
	 * @param f the number of horizontal frames
	 */
	public final void setHorizontalFrames(final int f) {
		this.horizFrames = f;
	}

	/**
	 * Gets the definition ID used by this definition.
	 * 
	 * @return the definition id
	 */
	public final int getDefID() {
		return defID;
	}

	/**
	 * Sets this definition's logic class.
	 * 
	 * @param clazz the new logic class
	 */
	public final void setLogicClass(final String clazz) {
		this.logicClass = clazz;
	}

	/**
	 * Gets the logic class for this entity definition.
	 * 
	 * @return the logic class
	 */
	public final String getLogicCLass() {
		return logicClass;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		super.writeData(pOut);
		pOut.writeInt(horizFrames);
		pOut.writeString(logicClass);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		super.readData(pIn);
		horizFrames = pIn.readInt();
		logicClass = pIn.readString();
	}

	@Override
	public final int getLength() {
		return super.getLength()
				+ SizeOf.INT
				+ PacketOutputStream
						.stringByteLength(logicClass);
	}
}
