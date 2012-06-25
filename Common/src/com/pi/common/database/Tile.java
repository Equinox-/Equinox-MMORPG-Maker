package com.pi.common.database;

import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * A class representing a tile of a sector.
 * 
 * @author Westin
 * 
 */
public class Tile implements PacketObject {
	/**
	 * The blocking flags for this tile.
	 */
	private int flags = 0;
	/**
	 * The attribute for this tile.
	 */
	private TileAttribute attribute = TileAttribute.NONE;
	/**
	 * The graphics for each tile layer.
	 */
	private GraphicsObject[] layers =
			new GraphicsObject[TileLayer.MAX_VALUE.ordinal()];

	/**
	 * Creates an empty tile.
	 */
	public Tile() {

	}

	/**
	 * Gets the directional blocking flags for this tile.
	 * 
	 * @return the blocking flags
	 */
	public final int getFlags() {
		return flags;
	}

	/**
	 * Sets the direction blocking flags for this tile.
	 * 
	 * @param sFlags the new flags
	 */
	public final void setFlags(final int sFlags) {
		this.flags = sFlags;
	}

	/**
	 * Gets this tile's attribute.
	 * 
	 * @return this tile's attribute
	 */
	public final TileAttribute getAttribute() {
		return attribute;
	}

	/**
	 * Sets this tile's attribute.
	 * 
	 * @param attrib the new attribute
	 */
	public final void setAttribute(final TileAttribute attrib) {
		attribute = attrib;
	}

	/**
	 * Checks if this tile's flags have the specified flag.
	 * 
	 * @param flag the flag to check
	 * @return <code>true</code> if the flag is on, <code>false</code> if the
	 *         flag is off
	 */
	public final boolean hasFlag(final int flag) {
		return (flags & flag) == flag;
	}

	/**
	 * Applies the specified flag to this tile's flags.
	 * 
	 * @param flag the flag to apply
	 */
	public final void applyFlag(final int flag) {
		flags |= flag;
	}

	/**
	 * Removes the specified flags from the flags list.
	 * 
	 * @param flag the flag to remove
	 */
	public final void removeFlag(final int flag) {
		flags &= (~flag);
	}

	/**
	 * Sets the graphics object on the given layer.
	 * 
	 * @param layer the layer to modify
	 * @param tile the graphics object to apply
	 */
	public final void setLayer(final TileLayer layer,
			final GraphicsObject tile) {
		layers[layer.ordinal()] = tile;
	}

	/**
	 * Gets the graphics object on the given layer.
	 * 
	 * @param layer the layer to fetch
	 * @return the graphics object on the given layer, or <code>null</code> if
	 *         none exists.
	 */
	public final GraphicsObject getLayer(final TileLayer layer) {
		if (layer.ordinal() < layers.length) {
			return layers[layer.ordinal()];
		} else {
			return null;
		}
	}

	@Override
	public final void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(flags);
		pOut.writeInt(attribute.ordinal());
		int layerFlags = 0;
		int stuff = 1;
		for (int i = 0; i < layers.length; i++) {
			if (layers[i] != null) {
				layerFlags ^= stuff;
			}
			stuff = stuff << 1;
		}
		pOut.writeInt(layerFlags);
		for (int i = 0; i < layers.length; i++) {
			if (layers[i] != null) {
				layers[i].writeData(pOut);
			}
		}
	}

	@Override
	public final void readData(final PacketInputStream pIn)
			throws IOException {
		flags = pIn.readInt();
		int attribID = pIn.readInt();
		if (attribID >= 0
				&& attribID < TileAttribute.values().length) {
			attribute = TileAttribute.values()[attribID];
		} else {
			attribute = TileAttribute.NONE;
		}
		int layerFlags = pIn.readInt();
		int stuff = 1;
		for (int i = 0; i < layers.length; i++) {
			if ((layerFlags & stuff) == stuff) {
				if (layers[i] == null) {
					layers[i] = new GraphicsObject();
				}
				layers[i].readData(pIn);
			} else {
				layers[i] = null;
			}
			stuff = stuff << 1;
		}
	}

	@Override
	public final int getLength() {
		int size = 3 * SizeOf.INT;
		for (int i = 0; i < layers.length; i++) {
			if (layers[i] != null) {
				size += layers[i].getLength();
			}
		}
		return size;
	}
}
