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
import java.util.Timer;
import java.util.TimerTask;

import com.pi.client.graphics.device.DisplayManager;
import com.pi.client.graphics.device.IGraphics;
import com.pi.client.graphics.device.awt.ImageManager.ImageStorage;
import com.pi.common.game.ObjectHeap;

public class AWTGraphics extends IGraphics {
    private volatile boolean graphicsRunning = false;
    private final Timer timer;
    private final TimerTask task;
    private final Applet applet;
    private long startTime;
    private int frameCont = 0;
    private int cFPS = 0;
    private ImageManager imageManager;

    public AWTGraphics(final DisplayManager mgr) {
	super(mgr);
	this.applet = mgr.getClient().getApplet();
	startTime = System.currentTimeMillis();
	imageManager = new ImageManager(mgr.getClient());

	timer = new Timer();
	// Create Timer Task
	task = new TimerTask() {
	    private Image backBuffer;
	    private int width, height;

	    @Override
	    public void run() {
		if (graphicsRunning) {
		    width = applet.getWidth();
		    height = applet.getHeight();
		    if (width > 0 && height > 0) {
			Graphics frontBuffer = (Graphics2D) applet
				.getGraphics();
			if (backBuffer == null
				|| backBuffer.getWidth(null) != width
				|| backBuffer.getHeight(null) != height) {
			    if (backBuffer != null)
				backBuffer.flush();
			    backBuffer = mgr.getClient().getApplet()
				    .createVolatileImage(width, height); // TODO
									 // Volatile
									 // vs
									 // Normal
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
				if (System.currentTimeMillis() - startTime >= 1000) {
				    cFPS = (int) (frameCont / ((System
					    .currentTimeMillis() - startTime) / 1000f));
				    startTime = System.currentTimeMillis();
				    frameCont = 0;
				}
				frontBuffer.drawImage(backBuffer, 0, 0, null);
			    }
			}
			if (frontBuffer != null) {
			    frontBuffer.dispose();
			}
		    }
		}
	    }
	};
	graphicsRunning = true;
	timer.schedule(task, 0, mgr.minMSPerFrame);
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
    public void drawImage(int img, int dx, int dy, int dwidth, int dheight,
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
	task.cancel();
	timer.cancel();
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
	return cFPS;
    }

    @Override
    public int getImageWidth(int graphic) {
	BufferedImage image = imageManager.fetchImage(graphic);
	if (image != null)
	    return image.getWidth();
	return 0;
    }

    @Override
    public int getImageHeight(int graphic) {
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
    public ObjectHeap<ImageStorage> loadedGraphics() {
	return imageManager.loadedMap();
    }
}
