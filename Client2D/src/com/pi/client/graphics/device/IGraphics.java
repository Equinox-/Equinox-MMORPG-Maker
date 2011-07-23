package com.pi.client.graphics.device;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import com.pi.common.database.GraphicsObject;

public abstract class IGraphics {
    protected final DisplayManager mgr;

    public IGraphics(DisplayManager mgr) {
	this.mgr = mgr;
    }

    protected void doRender() {
	mgr.doRender();
    }

    public abstract int getFPS();

    public abstract void drawLine(int x1, int y1, Color aC, int x2, int y2,
	    Color bC);

    public abstract void drawLine(int x1, int y1, int x2, int y2);

    public void drawLine(Point a, Color aC, Point b, Color bC) {
	drawLine(a.x, a.y, aC, b.x, b.y, bC);
    }

    public void drawLine(Point a, Point b) {
	drawLine(a.x, a.y, b.x, b.y);
    }

    public abstract void drawRect(int x, int y, int width, int height);

    public void drawRect(Rectangle r) {
	drawRect(r.x, r.y, r.width, r.height);
    }

    public abstract Rectangle2D getStringBounds(Font f, String string);

    public abstract void fillRect(int x, int y, int width, int height);

    public void fillRect(Rectangle r) {
	fillRect(r.x, r.y, r.width, r.height);
    }

    public abstract void drawImage(String graphic, int dx, int dy, int dwidth,
	    int dheight, int sx, int sy, int swidth, int sheight);

    public void drawImage(String graphic, int dx, int dy, int sx, int sy,
	    int swidth, int sheight) {
	drawImage(graphic, dx, dy, swidth, sheight, sx, sy, swidth, sheight);
    }

    public void drawImage(GraphicsObject obj, int dx, int dy, int dwidth,
	    int dheight) {
	drawImage(obj.getGraphic(), dx, dy, dwidth, dheight,
		(int) obj.getPositionX(), (int) obj.getPositionY(),
		(int) obj.getPositionWidth(), (int) obj.getPositionHeight());
    }

    public void drawImage(GraphicsObject obj, int dx, int dy) {
	drawImage(obj, dx, dy, (int) obj.getPositionWidth(),
		(int) obj.getPositionHeight());
    }

    public void drawImage(String graphic, int dx, int dy) {
	drawImage(graphic, dx, dy, 0, 0, getImageWidth(graphic),
		getImageHeight(graphic));
    }

    public void drawTiledImage(GraphicsObject bgImage, Rectangle bounds,
	    boolean stretchX, boolean stretchY) {
	int imgWidth = (int) bgImage.getPositionWidth();
	int imgHeight = (int) bgImage.getPositionHeight();
	int srcX = (int) bgImage.getPositionX();
	int srcY = (int) bgImage.getPositionY();
	if (stretchX && stretchY) {
	    drawImage(bgImage, bounds.x, bounds.y, bounds.width, bounds.height);
	} else if (stretchY) {
	    for (int x = bounds.x; x <= bounds.x + bounds.width; x += imgWidth + 1) {
		int drawWidth = Math.min(bounds.x + bounds.width - x, imgWidth);
		drawImage(bgImage.getGraphic(), x, bounds.y, drawWidth,
			bounds.height, srcX, srcY, drawWidth, imgHeight);
	    }
	} else if (stretchX) {
	    for (int y = bounds.y; y <= bounds.y + bounds.height; y += imgHeight + 1) {
		int drawHeight = Math.min(bounds.y + bounds.height - y,
			imgHeight);
		drawImage(bgImage.getGraphic(), bounds.x, y, bounds.width,
			drawHeight, srcX, srcY, imgWidth, drawHeight);
	    }
	} else {
	    for (int y = bounds.y; y <= bounds.y + bounds.height; y += imgHeight + 1) {
		for (int x = bounds.x; x <= bounds.x + bounds.width; x += imgWidth + 1) {
		    int drawWidth = Math.min(bounds.x + bounds.width - x,
			    imgWidth);
		    int drawHeight = Math.min(bounds.y + bounds.height - y,
			    imgHeight);
		    drawImage(bgImage.getGraphic(), x, y, srcX, srcY,
			    drawWidth, drawHeight);
		}
	    }
	}
    }

    public abstract int getImageWidth(String graphic);

    public abstract int getImageHeight(String graphic);

    public abstract void drawPoint(int x, int y);

    public abstract void setColor(Color color);

    public abstract void dispose();

    public abstract void drawText(String text, int x, int y, Font f, Color color);

    public void drawWrappedText(Rectangle rect, Font f, String textO,
	    Color color, boolean centered, boolean vAlign) {
	java.util.List<String> result = new java.util.ArrayList<String>();
	int height = 0;
	if (textO != null) {
	    for (String text : textO.split("\n")) {
		boolean hasMore = true;
		int current = 0;
		int lineBreak = -1;
		int nextSpace = -1;
		while (hasMore) {
		    while (true) {
			lineBreak = nextSpace;
			if (lineBreak == text.length() - 1) {
			    hasMore = false;
			    break;
			} else {
			    nextSpace = text.indexOf(' ', lineBreak + 1);
			    if (nextSpace == -1)
				nextSpace = text.length() - 1;
			    int linewidth = (int) getStringBounds(f,
				    text.substring(current, nextSpace))
				    .getWidth();
			    if (linewidth > rect.width)
				break;
			}
		    }
		    String line = text.substring(current, lineBreak + 1);
		    Rectangle2D bounds;
		    while ((bounds = getStringBounds(f, line)).getWidth() > rect.width) {
			String[] arr = splitByWidth(line, f, rect.width);
			result.add(arr[0]);
			line = arr[1];
			height += bounds.getHeight();
		    }
		    result.add(line);
		    height += bounds.getHeight();
		    current = lineBreak + 1;
		}
	    }
	}
	int y = (int) (vAlign ? rect.getCenterY() - (height / 2) : rect.getY());
	for (String line : result) {
	    Rectangle2D strBounds = getStringBounds(f, line);
	    if (y >= rect.getY()
		    && y + strBounds.getHeight() <= rect.getY()
			    + rect.getHeight()) {
		int sX = (int) (centered ? rect.getCenterX()
			- (strBounds.getWidth() / 2) : rect.getX());
		if (sX >= rect.getX()
			&& sX + strBounds.getWidth() <= rect.getX()
				+ rect.getWidth()) {
		    drawText(line, sX, y, f, color);
		    y += strBounds.getHeight();
		}
	    }
	}
    }

    public String[] splitByWidth(String s, Font f, int partAWidth) {
	int partBStart = s.length() / 2;
	while (true) {
	    try {
		String partA = s.substring(0, partBStart - 1);
		String partB = s.substring(partBStart);
		int width = (int) getStringBounds(f, partA).getWidth();
		if (width < partAWidth) {
		    return new String[] { partA, partB };
		} else {
		    partBStart /= 2;
		}
	    } catch (Exception e) {
		break;
	    }
	}
	return new String[] { "", s };
    }
}