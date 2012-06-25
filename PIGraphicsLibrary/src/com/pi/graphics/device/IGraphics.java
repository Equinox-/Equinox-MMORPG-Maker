package com.pi.graphics.device;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import com.pi.common.database.GraphicsObject;
import com.pi.common.game.ObjectHeap;

/**
 * Abstract class that graphics modes must extend.
 * 
 * @author Westin
 * 
 */
public abstract class IGraphics {
	/**
	 * The display manager this graphics instance is bound to.
	 */
	private final DisplayManager mgr;

	/**
	 * Creates a graphics instance that is bound to the specified display
	 * manager.
	 * 
	 * @param sMgr the display manager.
	 */
	public IGraphics(final DisplayManager sMgr) {
		this.mgr = sMgr;
	}

	/**
	 * Called to render to the buffer.
	 */
	protected final void doRender() {
		mgr.doRender();
	}

	/**
	 * Gets the current frames per second of this graphics object.
	 * 
	 * @return the frames per second
	 */
	public abstract int getFPS();

	/**
	 * Draws a line from point 1 with color a, and point 2 with color b.
	 * 
	 * @param x1 the starting x coordinate
	 * @param y1 the starting y coordinate
	 * @param aC the starting color
	 * @param x2 the ending x coordinate
	 * @param y2 the ending y coordinate
	 * @param bC the ending color
	 */
	public abstract void drawLine(int x1, int y1, Color aC,
			int x2, int y2, Color bC);

	/**
	 * Draws a line with the current color from point 1 to point 2.
	 * 
	 * @param x1 the starting x coordinate
	 * @param y1 the starting y coordinate
	 * @param x2 the ending x coordinate
	 * @param y2 the ending y coordinate
	 */
	public abstract void drawLine(int x1, int y1, int x2, int y2);

	/**
	 * Draws a line from point a with color aC to point b with color bC.
	 * 
	 * @param a the starting point
	 * @param aC the starting color
	 * @param b the ending point
	 * @param bC the ending color
	 */
	public final void drawLine(final Point a, final Color aC,
			final Point b, final Color bC) {
		drawLine(a.x, a.y, aC, b.x, b.y, bC);
	}

	/**
	 * Draws a line with the current color from point a to point b.
	 * 
	 * @param a the starting point
	 * @param b the ending point
	 */
	public final void drawLine(final Point a, final Point b) {
		drawLine(a.x, a.y, b.x, b.y);
	}

	/**
	 * Draws the border of a rectangle at the specified point with the specified
	 * size using the current color.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the rectangle's width
	 * @param height the rectangle's height
	 */
	public abstract void drawRect(int x, int y, int width,
			int height);

	/**
	 * Draws the border of the provided rectangle with the current color.
	 * 
	 * @param r the rectangle
	 */
	public final void drawRect(final Rectangle r) {
		drawRect(r.x, r.y, r.width, r.height);
	}

	/**
	 * Gets the bounds of a string with the given font.
	 * 
	 * @param f the font to use
	 * @param string the string to use
	 * @return the bounds of the string
	 */
	public abstract Rectangle2D getStringBounds(Font f,
			String string);

	/**
	 * Fills a rectangle with the current color, at the specified point, with
	 * the specified size.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the rectangle's width
	 * @param height the rectangle's height
	 */
	public abstract void fillRect(int x, int y, int width,
			int height);

	/**
	 * Fills the specified rectangle with the current color.
	 * 
	 * @param r the rectangle
	 */
	public final void fillRect(final Rectangle r) {
		fillRect(r.x, r.y, r.width, r.height);
	}

	/**
	 * Draws the image represented by the given graphical id number and source
	 * rectangle into the given destination rectangle.
	 * 
	 * @param graphic the graphics id number
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 * @param dwidth the destination width
	 * @param dheight the destination height
	 * @param sx the source x coordinate
	 * @param sy the source y coordinate
	 * @param swidth the source width
	 * @param sheight the source height
	 */
	public abstract void drawImage(int graphic, int dx, int dy,
			int dwidth, int dheight, int sx, int sy, int swidth,
			int sheight);

	/**
	 * Draws the image represented by the given graphical id number and source
	 * rectangle into the given destination rectangle with the given opacity.
	 * 
	 * @param graphic the graphics id number
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 * @param dwidth the destination width
	 * @param dheight the destination height
	 * @param sx the source x coordinate
	 * @param sy the source y coordinate
	 * @param swidth the source width
	 * @param sheight the source height
	 * @param opacity the alpha mask
	 */
	public abstract void drawFilteredImage(int graphic, int dx,
			int dy, int dwidth, int dheight, int sx, int sy,
			int swidth, int sheight, float opacity);

	/**
	 * Draws the image represented by the given graphical id number and source
	 * rectangle to the given destination point.
	 * 
	 * @param graphic the graphics id number
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 * @param sx the source x coordinate
	 * @param sy the source y coordinate
	 * @param swidth the source width
	 * @param sheight the source height
	 */
	public final void drawImage(final int graphic, final int dx,
			final int dy, final int sx, final int sy,
			final int swidth, final int sheight) {
		drawImage(graphic, dx, dy, swidth, sheight, sx, sy,
				swidth, sheight);
	}

	/**
	 * Draws the specified graphics object into the given destination rectangle.
	 * 
	 * @param obj the source object
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 * @param dwidth the destination width
	 * @param dheight the destination height
	 */
	public final void drawImage(final GraphicsObject obj,
			final int dx, final int dy, final int dwidth,
			final int dheight) {
		drawImage(obj.getGraphic(), dx, dy, dwidth, dheight,
				(int) obj.getPositionX(),
				(int) obj.getPositionY(),
				(int) obj.getPositionWidth(),
				(int) obj.getPositionHeight());
	}

	/**
	 * Draws the specified graphics object to the given destination point.
	 * 
	 * @param obj the source object
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 */
	public final void drawImage(final GraphicsObject obj,
			final int dx, final int dy) {
		drawImage(obj, dx, dy, (int) obj.getPositionWidth(),
				(int) obj.getPositionHeight());
	}

	/**
	 * Draws the image represented by the given graphical id number to the given
	 * destination point.
	 * 
	 * @param graphic the source graphic id
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 */
	public final void drawImage(final int graphic, final int dx,
			final int dy) {
		drawImage(graphic, dx, dy, 0, 0, getImageWidth(graphic),
				getImageHeight(graphic));
	}

	/**
	 * Draws the image represented by the given graphical id number and source
	 * rectangle to the given destination point.
	 * 
	 * @param graphic the graphics id number
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 * @param sx the source x coordinate
	 * @param sy the source y coordinate
	 * @param swidth the source width
	 * @param sheight the source height
	 * @param opacity the alpha mask
	 */
	public final void drawFilteredImage(final int graphic,
			final int dx, final int dy, final int sx,
			final int sy, final int swidth, final int sheight,
			final float opacity) {
		drawFilteredImage(graphic, dx, dy, swidth, sheight, sx,
				sy, swidth, sheight, opacity);
	}

	/**
	 * Draws the specified graphics object into the given destination rectangle
	 * with the given opacity.
	 * 
	 * @param obj the source graphics object
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 * @param dwidth the destination width
	 * @param dheight the destination height
	 * @param opacity the alpha mask
	 */
	public final void drawFilteredImage(
			final GraphicsObject obj, final int dx,
			final int dy, final int dwidth, final int dheight,
			final float opacity) {
		drawFilteredImage(obj.getGraphic(), dx, dy, dwidth,
				dheight, (int) obj.getPositionX(),
				(int) obj.getPositionY(),
				(int) obj.getPositionWidth(),
				(int) obj.getPositionHeight(), opacity);
	}

	/**
	 * Draws the specified graphics object to the given destination point with
	 * the given opacity.
	 * 
	 * @param obj the source graphics object
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 * @param opacity the alpha mask
	 */
	public final void drawFilteredImage(
			final GraphicsObject obj, final int dx,
			final int dy, final float opacity) {
		drawFilteredImage(obj, dx, dy,
				(int) obj.getPositionWidth(),
				(int) obj.getPositionHeight(), opacity);
	}

	/**
	 * Draws the image represented by the given graphical id number to the given
	 * destination point with the given opacity.
	 * 
	 * @param graphic the graphics id number
	 * @param dx the destination x coordinate
	 * @param dy the destination y coordinate
	 * @param opacity the alpha mask
	 */
	public final void drawFilteredImage(final int graphic,
			final int dx, final int dy, final float opacity) {
		drawFilteredImage(graphic, dx, dy, 0, 0,
				getImageWidth(graphic), getImageHeight(graphic),
				opacity);
	}

	/**
	 * Fills the bounds rectangle with the given background image, stretching it
	 * along the x and y axes as specified.
	 * 
	 * @param bgImage the image to tile
	 * @param bounds the rectangle to fill
	 * @param stretchX stretch the image on the x axis
	 * @param stretchY streth the image on the y axis
	 */
	public final void drawTiledImage(
			final GraphicsObject bgImage,
			final Rectangle bounds, final boolean stretchX,
			final boolean stretchY) {
		int imgWidth = (int) bgImage.getPositionWidth();
		int imgHeight = (int) bgImage.getPositionHeight();
		int srcX = (int) bgImage.getPositionX();
		int srcY = (int) bgImage.getPositionY();
		if (stretchX && stretchY) {
			drawImage(bgImage, bounds.x, bounds.y, bounds.width,
					bounds.height);
		} else if (stretchY) {
			for (int x = bounds.x; x <= bounds.x + bounds.width; x +=
					imgWidth + 1) {
				int drawWidth =
						Math.min(bounds.x + bounds.width - x,
								imgWidth);
				drawImage(bgImage.getGraphic(), x, bounds.y,
						drawWidth, bounds.height, srcX, srcY,
						drawWidth, imgHeight);
			}
		} else if (stretchX) {
			for (int y = bounds.y; y <= bounds.y + bounds.height; y +=
					imgHeight + 1) {
				int drawHeight =
						Math.min(bounds.y + bounds.height - y,
								imgHeight);
				drawImage(bgImage.getGraphic(), bounds.x, y,
						bounds.width, drawHeight, srcX, srcY,
						imgWidth, drawHeight);
			}
		} else {
			for (int y = bounds.y; y <= bounds.y + bounds.height; y +=
					imgHeight + 1) {
				for (int x = bounds.x; x <= bounds.x
						+ bounds.width; x += imgWidth + 1) {
					int drawWidth =
							Math.min(
									bounds.x + bounds.width - x,
									imgWidth);
					int drawHeight =
							Math.min(bounds.y + bounds.height
									- y, imgHeight);
					drawImage(bgImage.getGraphic(), x, y, srcX,
							srcY, drawWidth, drawHeight);
				}
			}
		}
	}

	/**
	 * Gets the pixel width of the image represented by the given graphical id
	 * number.
	 * 
	 * @param graphic the graphical id number
	 * @return the image width
	 */
	public abstract int getImageWidth(int graphic);

	/**
	 * Gets the pixel height of the image represented by the given graphical id
	 * number.
	 * 
	 * @param graphic the graphical id number
	 * @return the image height
	 */
	public abstract int getImageHeight(int graphic);

	/**
	 * Draws a single pixel with the current color at the specified point.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public abstract void drawPoint(int x, int y);

	/**
	 * Sets the current color of this graphics object.
	 * 
	 * @param color the new color
	 */
	public abstract void setColor(Color color);

	/**
	 * Disposes this graphics object and releases any related resources.
	 */
	public abstract void dispose();

	/**
	 * Draws the specified string at the specified x and y position, with the
	 * given font and color.
	 * 
	 * @param text the string to render
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param f the font to render with
	 * @param color the color to render with
	 */
	public abstract void drawText(String text, int x, int y,
			Font f, Color color);

	/**
	 * Draws the text specified wrapped inside the given rectangle with the
	 * specified font, color, and alignment.
	 * 
	 * @param rect the rectangle to render in
	 * @param f the font to render with
	 * @param textO the text to render
	 * @param color the color to render the text in
	 * @param centered if the text should be horizontally centered.
	 * @param vAlign if the text should be vertically centered
	 */
	public final void drawWrappedText(final Rectangle rect,
			final Font f, final String textO, final Color color,
			final boolean centered, final boolean vAlign) {
		java.util.List<String> result =
				new java.util.ArrayList<String>();
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
							nextSpace =
									text.indexOf(' ',
											lineBreak + 1);
							if (nextSpace == -1) {
								nextSpace = text.length() - 1;
							}
							int linewidth =
									(int) getStringBounds(
											f,
											text.substring(
													current,
													nextSpace))
											.getWidth();
							if (linewidth > rect.width) {
								break;
							}
						}
					}
					String line =
							text.substring(current,
									lineBreak + 1);
					Rectangle2D bounds =
							getStringBounds(f, line);
					while (bounds.getWidth() > rect.width) {
						String[] arr =
								splitByWidth(line, f, rect.width);
						result.add(arr[0]);
						line = arr[1];
						height += bounds.getHeight();
						bounds = getStringBounds(f, line);
					}
					result.add(line);
					height += bounds.getHeight();
					current = lineBreak + 1;
				}
			}
		}
		int y;
		if (vAlign) {
			y = (int) (rect.getCenterY() - (height / 2));
		} else {
			y = (int) rect.getY();
		}
		for (String line : result) {
			Rectangle2D strBounds = getStringBounds(f, line);
			if (y >= rect.getY()
					&& y + strBounds.getHeight() <= rect.getY()
							+ rect.getHeight()) {
				int sX;
				if (centered) {
					sX =
							(int) (rect.getCenterX() - (strBounds
									.getWidth() / 2));
				} else {
					sX = (int) rect.getX();
				}
				if (sX >= rect.getX()
						&& sX + strBounds.getWidth() <= rect
								.getX() + rect.getWidth()) {
					drawText(line, sX, y, f, color);
					y += strBounds.getHeight();
				}
			}
		}
	}

	/**
	 * Splits a string by it's pixel width.
	 * 
	 * @param s string to split
	 * @param f the font to calculate with
	 * @param partAWidth the maximum pixel width of the first part of the
	 *            string.
	 * @return An array with two elements, the first being the part with the
	 *         specified pixel width, the second being the leftovers.
	 */
	protected final String[] splitByWidth(final String s,
			final Font f, final int partAWidth) {
		int partBStart = s.length() / 2;
		while (true) {
			try {
				String partA = s.substring(0, partBStart - 1);
				String partB = s.substring(partBStart);
				int width =
						(int) getStringBounds(f, partA)
								.getWidth();
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

	/**
	 * Sets the clipping area for this graphics object.
	 * 
	 * @param r the new clip area, or <code>null</code> if it should be reset
	 */
	public abstract void setClip(Rectangle r);

	/**
	 * Gets the current clipping area.
	 * 
	 * @return the clip area
	 */
	public abstract Rectangle getClip();

	/**
	 * Sets the clipping area for this graphics object.
	 * 
	 * @param x the x coordinate of the clip area
	 * @param y the y coordinate of the clip area
	 * @param width the width of the clip area
	 * @param height the height of the clip area
	 */
	public final void setClip(final int x, final int y,
			final int width, final int height) {
		setClip(new Rectangle(x, y, width, height));
	}

	/**
	 * Gets the currently loaded graphics objects.
	 * 
	 * @return the loaded graphics in a heap
	 */
	public abstract ObjectHeap<? extends GraphicsStorage> loadedGraphics();

	/**
	 * Gets the display manager this graphics instance is registered to.
	 * 
	 * @return the display manager
	 */
	protected final DisplayManager getDisplayManager() {
		return mgr;
	}
}