package com.pi.common.database;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import com.pi.common.contants.GlobalConstants;

public class GraphicsObject implements Serializable {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
    private String graphic;
    private float tX, tY, tWidth, tHeight;

    public GraphicsObject() {
    }

    public GraphicsObject(String graphic) {
	this.graphic = graphic;
    }

    public GraphicsObject(String graphic, float x, float y, float width, float height) {
	this.graphic = graphic;
	setPosition(x, y, width, height);
    }

    public void setGraphic(String graphic) {
	this.graphic = graphic;
    }

    public void setPosition(float x, float y, float width, float height) {
	this.tX = x;
	this.tY = y;
	this.tWidth = width;
	this.tHeight = height;
    }

    public String getGraphic() {
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
}
