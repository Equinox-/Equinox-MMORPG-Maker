package com.pi.common.database.def.entity;

import java.io.IOException;

import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

public class LogicDefComponent extends EntityDefComponent {
	/**
	 * The logic class name.
	 */
	private String logicClass;

	public LogicDefComponent() {

	}

	public LogicDefComponent(String clazz) {
		logicClass = clazz;
	}

	/**
	 * Sets this definition's logic class.
	 * 
	 * @param clazz
	 *            the new logic class
	 */
	public final void setLogicClass(final String clazz) {
		this.logicClass = clazz;
	}

	/**
	 * Gets the logic class for this entity definition.
	 * 
	 * @return the logic class
	 */
	public final String getLogicClass() {
		return logicClass;
	}

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeString(logicClass);
	}

	@Override
	public int getLength() {
		return PacketOutputStream.stringByteLength(logicClass);
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		logicClass = pIn.readString();
	}

	@Override
	public EntityComponent createDefaultComponent() {
		return null;
	}
}
