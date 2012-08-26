package com.pi.graphics.device.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Timer;
import java.util.TimerTask;

import com.pi.common.game.ObjectHeap;
import com.pi.graphics.device.DisplayManager;
import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.awt.ImageManager.ImageStorage;

/**
 * Class representing a Java 2D or Abstract Windowing Toolkit based graphics
 * object.
 * 
 * @author Westin
 * 
 */
public class AWTGraphics extends IGraphics {
	/**
	 * A boolean representing the running state of the graphics loop.
	 */
	private volatile boolean graphicsRunning = false;
	/**
	 * The timer controlling the graphics loop.
	 */
	private final Timer timer;
	/**
	 * A timer task hooked to the timer to render the buffer.
	 */
	private final TimerTask task;
	/**
	 * The last time the frames per second were updated.
	 */
	private long lastFPSUpdate;
	/**
	 * Number for frames rendered since last frames per second update.
	 */
	private int frameCount = 0;
	/**
	 * The cached frames per second value.
	 */
	private int cFPS = 0;
	/**
	 * The image manager used for loading images into the cache.
	 */
	private ImageManager imageManager;

	/**
	 * Creates an Java 2D or Abstract Windowing Toolkit graphics object linked
	 * to the given display manager.
	 * 
	 * @param mgr the display manager to bind to
	 */
	public AWTGraphics(final DisplayManager mgr) {
		super(mgr);
		lastFPSUpdate = System.currentTimeMillis();
		imageManager =
				new ImageManager(getDisplayManager().getSource());

		timer = new Timer();
		// Create the Timer Task
		task = new TimerTask() {
			private Image backBuffer;
			private int width, height;

			@Override
			public void run() {
				if (graphicsRunning) {
					width =
							getDisplayManager().getSource()
									.getContainer().getWidth();
					height =
							getDisplayManager().getSource()
									.getContainer().getHeight();
					if (width > 0 && height > 0) {
						Graphics frontBuffer =
								(Graphics2D) getDisplayManager()
										.getSource()
										.getContainer()
										.getGraphics();
						if (backBuffer == null
								|| backBuffer.getWidth(null) != width
								|| backBuffer.getHeight(null) != height) {
							if (backBuffer != null) {
								backBuffer.flush();
							}
							backBuffer =
									mgr.getSource()
											.getContainer()
											.createVolatileImage(
													width,
													height);
						}
						if (backBuffer != null) {
							graphics =
									(Graphics2D) backBuffer
											.getGraphics();
							if (graphics != null
									&& frontBuffer != null) {
								graphics.setBackground(Color.BLACK);
								graphics.clearRect(0, 0, mgr
										.getSource()
										.getContainer()
										.getWidth(), mgr
										.getSource()
										.getContainer()
										.getHeight());
								doRender();
								frameCount++;
								if (System.currentTimeMillis()
										- lastFPSUpdate >= 1000) {
									cFPS =
											(int) (frameCount / ((System
													.currentTimeMillis() - lastFPSUpdate) / 1000f));
									lastFPSUpdate =
											System.currentTimeMillis();
									frameCount = 0;
								}
								frontBuffer.drawImage(
										backBuffer, 0, 0, null);
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
		timer.schedule(task, 0,
				DisplayManager.MINIMUM_MILLISECONDS_PER_FRAME);
	}

	/**
	 * The graphics instance wrapped by this graphics object.
	 */
	private Graphics2D graphics;

	@Override
	public final void drawLine(final int x1, final int y1,
			final Color aC, final int x2, final int y2,
			final Color bC) {
		Graphics2D sGraphics =
				(Graphics2D) this.graphics.create();
		sGraphics.setPaint(new GradientPaint(x1, y1, aC, x2, y2,
				bC));
		sGraphics.drawLine(x1, y1, x2, y2);
		sGraphics.dispose();
	}

	@Override
	public final void drawLine(final int x1, final int y1,
			final int x2, final int y2) {
		graphics.drawLine(x1, y1, x2, y2);
	}

	@Override
	public final void drawRect(final int x, final int y,
			final int width, final int height) {
		graphics.drawRect(x, y, width, height);
	}

	@Override
	public final void fillRect(final int x, final int y,
			final int width, final int height) {
		graphics.fillRect(x, y, width, height);
	}

	@Override
	public final void drawImage(final int img, final int dx,
			final int dy, final int dwidth, final int dheight,
			final int sx, final int sy, final int swidth,
			final int sheight) {
		Image image = imageManager.fetchImage(img);
		if (image != null) {
			graphics.drawImage(image, dx, dy, dx + dwidth, dy
					+ dheight, sx, sy, sx + swidth,
					sy + sheight, null);
		}
	}

	@Override
	public final void drawPoint(final int x, final int y) {
		graphics.drawRect(x, y, 1, 1);
	}

	@Override
	public final void setColor(final Color color) {
		graphics.setColor(color);
	}

	@Override
	public final void dispose() {
		graphicsRunning = false;
		task.cancel();
		timer.cancel();
		imageManager.dispose();
	}

	@Override
	public final void drawText(final String text, final int x,
			final int y, final Font f, final Color color) {
		if (color != null) {
			graphics.setColor(color);
		}
		if (f != null) {
			graphics.setFont(f);
		}
		graphics.drawString(text, x, y
				+ (int) graphics.getFontMetrics()
						.getStringBounds(text, graphics)
						.getHeight());
	}

	@Override
	public final int getFPS() {
		return cFPS;
	}

	@Override
	public final int getImageWidth(final int graphic) {
		BufferedImage image = imageManager.fetchImage(graphic);
		if (image != null) {
			return image.getWidth();
		}
		return 0;
	}

	@Override
	public final int getImageHeight(final int graphic) {
		BufferedImage image = imageManager.fetchImage(graphic);
		if (image != null) {
			return image.getHeight();
		}
		return 0;
	}

	@Override
	public final Rectangle2D getStringBounds(final Font f,
			final String s) {
		return graphics.getFontMetrics(f).getStringBounds(s,
				graphics);
	}

	@Override
	public final void setClip(final Rectangle r) {
		if (r == null) {
			graphics.setClip(getDisplayManager().getSource()
					.getContainer().getBounds());
		} else {
			graphics.setClip(r);
		}
	}

	@Override
	public final Rectangle getClip() {
		Rectangle r = graphics.getClipBounds();
		if (r == null) {
			r =
					getDisplayManager().getSource()
							.getContainer().getBounds();
		}
		return r;
	}

	@Override
	public final ObjectHeap<ImageStorage> loadedGraphics() {
		return imageManager.loadedMap();
	}

	@Override
	public final void drawFilteredImage(final int graphic,
			final int dx, final int dy, final int dwidth,
			final int dheight, final int sx, final int sy,
			final int swidth, final int sheight,
			final float opacity) {
		BufferedImage raw = imageManager.fetchImage(graphic);
		if (raw != null) {
			RescaleOp op =
					new RescaleOp(new float[] { 1f, 1f, 1f,
							opacity },
							new float[] { 0, 0, 0, 0 }, null);
			BufferedImage image =
					op.createCompatibleDestImage(raw, null);
			image = op.filter(raw, image);
			graphics.drawImage(image, dx, dy, dx + dwidth, dy
					+ dheight, sx, sy, sx + swidth,
					sy + sheight, null);
		}
	}

	@Override
	public Component getCanvas() {
		return getDisplayManager().getSource().getContainer();
	}
}
