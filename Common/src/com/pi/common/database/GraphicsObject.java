package com.pi.common.database;

import java.awt.geom.Rectangle2D;
import java.io.IOException;

import com.pi.common.net.PacketInputStream;
import com.pi.common.net.PacketOutputStream;
import com.pi.common.net.packet.PacketObject;

public class GraphicsObject implements PacketObject {
	private int graphic;
	private float tX, tY, tWidth, tHeight;

	public GraphicsObject() {
	}

	public GraphicsObject(int graphic) {
		this.graphic = graphic;
	}

	public GraphicsObject(int graphic, float x, float y, float width,
			float height) {
		this.graphic = graphic;
		setPosition(x, y, width, height);
	}

	public void setGraphic(int graphic) {
		this.graphic = graphic;
	}

	public void setPosition(float x, float y, float width, float height) {
		this.tX = x;
		this.tY = y;
		this.tWidth = width;
		this.tHeight = height;
	}

	public int getGraphic() {
		return graphic;
	}

	public Rectangle2D getPosition(Rectangle2D rect) {
		rect.setFrame(getPositionX(), getPositionY(), getPositionWidth(),
				getPositionHeight());
		return rect;
	}

	public Rectangle2D getPosition() {
		return getPosition(new Rectangle2D.Float());
	}

	public float getPositionX() {
		return tX;
	}

	public float getPositionY() {
		return tY;
	}

	public float getPositionWidth() {
		return tWidth;
	}

	public float getPositionHeight() {
		return tHeight;
	}

	@Override
	public void writeData(PacketOutputStream pOut) throws IOException {
		pOut.writeInt(graphic);
		pOut.writeFloat(tX);
		pOut.writeFloat(tY);
		pOut.writeFloat(tWidth);
		pOut.writeFloat(tHeight);
	}

	@Override
	public void readData(PacketInputStream pIn) throws IOException {
		graphic = pIn.readInt();
		tX = pIn.readFloat();
		tY = pIn.readFloat();
		tWidth = pIn.readFloat();
		tHeight = pIn.readFloat();
	}

	@Override
	public int getLength() {
		return 20;
	}
}
