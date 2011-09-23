package com.pi.client.graphics.device.awt;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;

import com.pi.client.graphics.device.DisplayManager;
import com.pi.client.graphics.device.GraphicsStorage;
import com.pi.client.graphics.device.IGraphics;

public class AWTGraphics extends IGraphics {
    private boolean graphicsRunning = true;
    private final Thread graphicsLoop;
    private final Applet applet;
    private long startTime;
    private int frameCont = 0;
    private ImageManager imageManager;

    public AWTGraphics(final DisplayManager mgr) {
	super(mgr);
	this.applet = mgr.getClient().getApplet();
	startTime = System.currentTimeMillis();
	imageManager = new ImageManager(mgr.getClient());
	graphicsLoop = new Thread(mgr.getClient().getThreadGroup(),null,"PI Graphics Thread") {
	    private Image backBuffer;
	    private int width, height;

	    @Override
	    public void run() {
		mgr.getClient().getLog().fine("Starting Graphics Thread");
		while (graphicsRunning) {
		    width = applet.getWidth();
		    height = applet.getHeight();
		    if (width > 0 && height > 0) {
			Graphics frontBuffer = (Graphics2D) applet
				.getGraphics();
			if (backBuffer == null
				|| backBuffer.getWidth(null) != width
				|| backBuffer.getHeight(null) != height) {
			    backBuffer = mgr.getClient().getApplet()
				    .createImage(width, height);
			}
			if (backBuffer != null) {
			    graphics = (Graphics2D) backBuffer.getGraphics();
			    if (graphics != null && frontBuffer != null) {
				graphics.setBackground(Color.BLACK);
				graphics.clearRect(0, 0, mgr.getClient()
					.getApplet().getWidth(), mgr
					.getClient().getApplet().getHeight());
				doRender();
				frameCont++;
				frontBuffer.drawImage(backBuffer, 0, 0, null);
			    }
			}
			if (frontBuffer != null) {
			    frontBuffer.dispose();
			}
		    }
		}
		mgr.getClient().getLog().fine("Killing Graphics Thread");
	    }
	};
	graphicsLoop.start();
    }

    private Graphics2D graphics;

    @Override
    public void drawLine(int x1, int y1, Color aC, int x2, int y2, Color bC) {
	Graphics2D graphics = (Graphics2D) this.graphics.create();
	graphics.setPaint(new GradientPaint(x1, y1, aC, x2, y2, bC));
	graphics.drawLine(x1, y1, x2, y2);
	graphics.dispose();
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
	graphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
	graphics.drawRect(x, y, width, height);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
	graphics.fillRect(x, y, width, height);
    }

    @Override
    public void drawImage(String img, int dx, int dy, int dwidth, int dheight,
	    int sx, int sy, int swidth, int sheight) {
	Image image = imageManager.fetchImage(img);
	if (image != null)
	    graphics.drawImage(image, dx, dy, dx + dwidth, dy + dheight, sx,
		    sy, sx + swidth, sy + sheight, null);
    }

    @Override
    public void drawPoint(int x, int y) {
	graphics.drawRect(x, y, 1, 1);
    }

    @Override
    public void setColor(Color color) {
	graphics.setColor(color);
    }

    @Override
    public void dispose() {
	graphicsRunning = false;
	try {
	    graphicsLoop.join();
	} catch (InterruptedException e) {
	    e.printStackTrace(client.getLog().getErrorStream());
	    System.exit(0);
	}
	imageManager.dispose();
    }

    @Override
    public void drawText(String text, int x, int y, Font f, Color color) {
	if (color != null)
	    graphics.setColor(color);
	if (f != null)
	    graphics.setFont(f);
	graphics.drawString(
		text,
		x,
		y
			+ (int) graphics.getFontMetrics()
				.getStringBounds(text, graphics).getHeight());
    }

    @Override
    public int getFPS() {
	long duration = (System.currentTimeMillis() - startTime) / 1000;
	if (duration != 0) {
	    return (int) (frameCont / duration);
	}
	return 0;
    }

    @Override
    public int getImageWidth(String graphic) {
	BufferedImage image = imageManager.fetchImage(graphic);
	if (image != null)
	    return image.getWidth();
	return 0;
    }

    @Override
    public int getImageHeight(String graphic) {
	BufferedImage image = imageManager.fetchImage(graphic);
	if (image != null)
	    return image.getHeight();
	return 0;
    }

    @Override
    public Rectangle2D getStringBounds(Font f, String s) {
	return graphics.getFontMetrics(f).getStringBounds(s, graphics);
    }

    @Override
    public void setClip(Rectangle r) {
	if (r == null)
	    r = mgr.getClient().getApplet().getBounds();
	graphics.setClip(r);
    }

    @Override
    public Rectangle getClip() {
	Rectangle r = graphics.getClipBounds();
	if (r == null)
	    r = mgr.getClient().getApplet().getBounds();
	return r;
    }

	@Override
	public Map<String, ? extends GraphicsStorage> loadedGraphics() {
		return imageManager.loadedMap();
	}
}
