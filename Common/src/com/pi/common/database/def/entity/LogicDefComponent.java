package com.pi.common.database.def.entity;

import java.io.IOException;

import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;

/**
 * An entity definition component for defining entities that have server logic
 * paired with them.
 * 
 * @author westin
 * 
 */
public class LogicDefComponent extends EntityDefComponent {
	/**
	 * The logic class name.
	 */
	private String logicClass;

	/**
	 * Creates a blank logic definition.
	 */
	public LogicDefComponent() {
	}

	/**
	 * Creates a logic definition component for the given logic class.
	 * 
	 * @param clazz the logic class's absolute name
	 */
	public LogicDefComponent(final String clazz) {
		logicClass = clazz;
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
	public final String getLogicClass() {
		return logicClass;
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeString(logicClass);
	}

	@Override
	public final int getLength() {
		return PacketOutputStream.stringByteLength(logicClass);
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		logicClass = pIn.readString();
	}

	@Override
	public final EntityComponent[] createDefaultComponents() {
		return null;
	}
}
