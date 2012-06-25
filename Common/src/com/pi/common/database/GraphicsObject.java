package com.pi.common.database;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

import com.pi.common.contants.NetworkConstants.SizeOf;
import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

/**
 * A class representing a graphical sub image.
 * 
 * @author Westin
 * 
 */
public class GraphicsObject implements PacketObject {
	/**
	 * The graphic id.
	 */
	private int graphic;
	/**
	 * The graphics position.
	 */
	private float tX, tY, tWidth, tHeight;

	/**
	 * Create a blank graphics object that still needs to be assigned a graphics
	 * id and position.
	 */
	public GraphicsObject() {
	}

	/**
	 * Create a graphics object that still needs to be assigned a graphics
	 * position.
	 * 
	 * @param sGraphic the graphics id
	 */
	public GraphicsObject(final int sGraphic) {
		this.graphic = sGraphic;
	}

	/**
	 * Creates a graphics object with all the necessary variables assigned.
	 * 
	 * @param sGraphic the graphics id
	 * @param x the x position
	 * @param y the y position
	 * @param width the width position
	 * @param height the height position
	 */
	public GraphicsObject(final int sGraphic, final float x,
			final float y, final float width, final float height) {
		this.graphic = sGraphic;
		setPosition(x, y, width, height);
	}

	/**
	 * Sets the graphics id.
	 * 
	 * @param sGraphic the graphics id
	 */
	public final void setGraphic(final int sGraphic) {
		this.graphic = sGraphic;
	}

	/**
	 * Sets the graphics position.
	 * 
	 * @param x the x position
	 * @param y the y position
	 * @param width the width
	 * @param height the height
	 */
	public void setPosition(final float x, final float y,
			final float width, final float height) {
		this.tX = x;
		this.tY = y;
		this.tWidth = width;
		this.tHeight = height;
	}

	/**
	 * Gets the current graphics id.
	 * 
	 * @return the graphics id
	 */
	public final int getGraphic() {
		return graphic;
	}

	/**
	 * Gets the position and assigns it to the provided rectangle.
	 * 
	 * @param rect the rectangle to assign to
	 * @return the assigned rectangle
	 */
	public final Rectangle2D getPosition(final Rectangle2D rect) {
		rect.setFrame(getPositionX(), getPositionY(),
				getPositionWidth(), getPositionHeight());
		return rect;
	}

	/**
	 * Gets the position and assigns it to a new rectangle.
	 * 
	 * @return the position
	 */
	public final Rectangle2D getPosition() {
		return getPosition(new Rectangle2D.Float());
	}

	/**
	 * Gets the x position of this graphics object.
	 * 
	 * @return the x position
	 */
	public float getPositionX() {
		return tX;
	}

	/**
	 * Gets the y position of this graphics object.
	 * 
	 * @return the y position
	 */
	public float getPositionY() {
		return tY;
	}

	/**
	 * Gets the width of this graphics object.
	 * 
	 * @return this object's width
	 */
	public float getPositionWidth() {
		return tWidth;
	}

	/**
	 * Gets the height of this graphics object.
	 * 
	 * @return this object's height
	 */
	public float getPositionHeight() {
		return tHeight;
	}

	@Override
	public void writeData(final PacketOutputStream pOut)
			throws IOException {
		pOut.writeInt(graphic);
		pOut.writeFloat(tX);
		pOut.writeFloat(tY);
		pOut.writeFloat(tWidth);
		pOut.writeFloat(tHeight);
	}

	@Override
	public void readData(final PacketInputStream pIn)
			throws IOException {
		graphic = pIn.readInt();
		tX = pIn.readFloat();
		tY = pIn.readFloat();
		tWidth = pIn.readFloat();
		tHeight = pIn.readFloat();
	}

	@Override
	public int getLength() {
		return (4 * SizeOf.FLOAT) + SizeOf.INT;
	}
}
