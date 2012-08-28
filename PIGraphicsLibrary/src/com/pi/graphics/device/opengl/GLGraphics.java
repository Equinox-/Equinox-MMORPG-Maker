package com.pi.graphics.device.opengl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.pi.common.game.ObjectHeap;
import com.pi.graphics.device.DisplayManager;
import com.pi.graphics.device.GraphicsStorage;
import com.pi.graphics.device.IGraphics;

/**
 * Class representing an Open Graphics Library based graphics object.
 * 
 * @author Westin
 * 
 */
public class GLGraphics extends IGraphics implements
		GLEventListener {
	/**
	 * The animator object that renders the buffer.
	 */
	private final FPSAnimator animator;
	/**
	 * The thread that provides text renderer instances for different fonts.
	 */
	private final TextRendererProvider txtRender;
	/**
	 * The texture manager used for loading textures into the cache.
	 */
	private final GLImageManager textureManager;
	/**
	 * The current clip area of this graphics object.
	 */
	private Rectangle cliparea;
	/**
	 * The current GL object being wrapped.
	 */
	private GL2 gl;
	/**
	 * The OpenGL drawable object this the current GL object is derived from.
	 */
	private GLAutoDrawable glCore;
	/**
	 * The OpenGL canvas object being rendered to.
	 */
	private final GLCanvas canvas;

	/**
	 * Creates an OpenGL based graphics object bound to the specified display
	 * manager.
	 * 
	 * @param mgr the display manager to bind this to
	 */
	public GLGraphics(final DisplayManager mgr) {
		super(mgr);
		canvas = new GLCanvas();
		getDisplayManager().getSource().getContainer()
				.add(getCanvas());
		canvas.addGLEventListener(this);
		canvas.setSize(getDisplayManager().getSource()
				.getContainer().getSize());
		cliparea = getCanvas().getBounds();
		canvas.setLocation(0, 0);
		animator =
				new FPSAnimator(
						DisplayManager.MAXIMUM_FRAMES_PER_SECOND);
		animator.add(canvas);
		animator.start();
		mgr.getSource().getLog().fine("Started graphics thread");
		txtRender = new TextRendererProvider(mgr.getSource());
		textureManager = new GLImageManager(mgr.getSource());
	}

	/**
	 * Get the drawable core that the current GL object is derived from.
	 * 
	 * @return the OpenGL core drawable
	 */
	public final GLAutoDrawable getCore() {
		return glCore;
	}

	@Override
	public final void drawLine(final int x1, final int y1,
			final Color aC, final int x2, final int y2,
			final Color bC) {
		if (!animator.isAnimating()) {
			return;
		}
		gl.glBegin(GL.GL_LINES);
		gl.glColor4i(aC.getRed(), aC.getGreen(), aC.getBlue(),
				aC.getAlpha());
		gl.glVertex2f(x1, y1);
		gl.glColor4i(bC.getRed(), bC.getGreen(), bC.getBlue(),
				bC.getAlpha());
		gl.glVertex2f(x2, y2);
		gl.glEnd();
	}

	@Override
	public final void drawLine(final int x1, final int y1,
			final int x2, final int y2) {
		if (!animator.isAnimating()) {
			return;
		}
		gl.glBegin(GL.GL_LINES);
		gl.glVertex2f(x1, y1);
		gl.glVertex2f(x2, y2);
		gl.glEnd();
	}

	@Override
	public final void drawRect(final int x, final int y,
			final int width, final int height) {
		if (!animator.isAnimating()) {
			return;
		}
		gl.glBegin(GL2.GL_LINE_LOOP);
		gl.glVertex2f(x, y);
		gl.glVertex2f(x + width, y);
		gl.glVertex2f(x + width, (y + height));
		gl.glVertex2f(x, (y + height));
		gl.glEnd();
	}

	@Override
	public final void fillRect(final int x, final int y,
			final int width, final int height) {
		if (!animator.isAnimating()) {
			return;
		}
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(x, y);
		gl.glVertex2f(x + width, y);
		gl.glVertex2f(x + width, (y + height));
		gl.glVertex2f(x, (y + height));
		gl.glEnd();
	}

	@Override
	public final void drawImage(final int texID, final int dx,
			final int dy, final int dwidth, final int dheight,
			final int sx, final int sy, final int swidth,
			final int sheight) {
		drawFilteredImage(texID, dx, dy, dwidth, dheight, sx,
				sy, swidth, sheight, 1f);
	}

	@Override
	public final void drawFilteredImage(final int texID,
			final int dx, final int dy, final int dwidth,
			final int dheight, final int sx, final int sy,
			final int swidth, final int sheight,
			final float opacity) {
		if (!animator.isAnimating()) {
			return;
		}
		Texture tex = textureManager.fetchImage(texID);
		if (tex != null) {
			TextureCoords coords = tex.getImageTexCoords();
			float width = coords.right() - coords.left();
			float height = coords.bottom() - coords.top();
			tex.enable(gl);
			tex.bind(gl);
			setColor(new Color(1f, 1f, 1f, opacity));
			gl.glBegin(GL2.GL_QUADS);
			gl.glTexCoord2d((sx / width) + coords.left(),
					(sy / height) + coords.top());
			gl.glVertex2f(dx, dy);
			gl.glTexCoord2d(
					((sx + swidth) / width) + coords.left(),
					(sy / height) + coords.top());
			gl.glVertex2f(dx + dwidth, dy);
			gl.glTexCoord2d(
					((sx + swidth) / width) + coords.left(),
					((sy + sheight) / height) + coords.top());
			gl.glVertex2f(dx + dwidth, (dy + dheight));
			gl.glTexCoord2d((sx / width) + coords.left(),
					((sy + sheight) / height) + coords.top());
			gl.glVertex2f(dx, (dy + dheight));
			gl.glEnd();
			tex.disable(gl);
		}
	}

	@Override
	public final void drawPoint(final int x, final int y) {
		if (!animator.isAnimating()) {
			return;
		}
		gl.glBegin(GL.GL_POINTS);
		gl.glVertex2f(x, y);
		gl.glEnd();
	}

	@Override
	public final void setColor(final Color color) {
		if (!animator.isAnimating()) {
			return;
		}
		gl.glColor4f(color.getRed() / 255f,
				color.getGreen() / 255f, color.getBlue() / 255f,
				color.getAlpha() / 255f);
	}

	@Override
	public final void display(final GLAutoDrawable arg0) {
		if (!getCanvas().getSize().equals(
				getDisplayManager().getSource().getContainer()
						.getSize())) {
			getCanvas().setSize(
					getDisplayManager().getSource()
							.getContainer().getSize());
			cliparea = getCanvas().getBounds();
			canvas.reshape(0, 0, getDisplayManager().getSource()
					.getContainer().getWidth(),
					getDisplayManager().getSource()
							.getContainer().getHeight());
		}
		this.gl = arg0.getGL().getGL2();
		this.glCore = arg0;
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, getCanvas().getWidth(), getCanvas()
				.getHeight(), 0, 1, -1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT
				| GL.GL_DEPTH_BUFFER_BIT);
		super.doRender();
	}

	@Override
	public final void dispose(final GLAutoDrawable arg0) {
		this.gl = arg0.getGL().getGL2();
		this.glCore = arg0;
		txtRender.dispose();
		textureManager.dispose();
	}

	@Override
	public final void init(final GLAutoDrawable arg0) {
		this.gl = arg0.getGL().getGL2();
		this.glCore = arg0;
		this.gl.setSwapInterval(1);
		this.gl.glEnable(GL.GL_BLEND);
		this.gl.glBlendFunc(GL.GL_SRC_ALPHA,
				GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public final void reshape(final GLAutoDrawable arg0,
			final int arg1, final int arg2, final int arg3,
			final int arg4) {
		this.gl = arg0.getGL().getGL2();
		this.glCore = arg0;
	}

	@Override
	public final void dispose() {
		if (canvas != null
				&& (!animator.isStarted() || animator.stop())) {
			canvas.destroy();
		}
		getDisplayManager().getSource().getLog()
				.fine("Killed OpenGL Graphics thread");
	}

	@Override
	public final void drawText(final String text, final int x,
			final int y, final Font f, final Color color) {
		if (!animator.isAnimating()) {
			return;
		}
		TextRenderer tRender = txtRender.fetchRenderer(f);
		tRender.beginRendering(getCanvas().getWidth(),
				getCanvas().getHeight());
		tRender.setColor(color);
		tRender.draw(text, x, getCanvas().getHeight() - y
				- (int) tRender.getBounds(text).getHeight());
		tRender.endRendering();
	}

	@Override
	public final Rectangle2D getStringBounds(final Font f,
			final String s) {
		return txtRender.fetchRenderer(f).getBounds(s);
	}

	@Override
	public final int getFPS() {
		return (int) animator.getLastFPS();
	}

	@Override
	public final int getImageWidth(final int graphic) {
		Texture tex = textureManager.fetchImage(graphic);
		if (tex != null) {
			return tex.getWidth();
		}
		return 0;
	}

	@Override
	public final int getImageHeight(final int graphic) {
		Texture tex = textureManager.fetchImage(graphic);
		if (tex != null) {
			return tex.getHeight();
		}
		return 0;
	}

	@Override
	public final void setClip(final Rectangle r) {
		if (!animator.isAnimating()) {
			return;
		}
		if (r != null) {
			gl.glClipPlane(GL2.GL_CLIP_PLANE0, new double[] { 1,
					0, 0, -r.x }, 0);
			gl.glClipPlane(GL2.GL_CLIP_PLANE1, new double[] { 0,
					1, 0, -r.y }, 0);
			gl.glClipPlane(GL2.GL_CLIP_PLANE2, new double[] {
					-1, 0, 0, r.x + r.width }, 0);
			gl.glClipPlane(GL2.GL_CLIP_PLANE3, new double[] { 0,
					-1, 0, r.y + r.height }, 0);
			gl.glEnable(GL2.GL_CLIP_PLANE0);
			gl.glEnable(GL2.GL_CLIP_PLANE1);
			gl.glEnable(GL2.GL_CLIP_PLANE2);
			gl.glEnable(GL2.GL_CLIP_PLANE3);
		} else {
			gl.glDisable(GL2.GL_CLIP_PLANE0);
			gl.glDisable(GL2.GL_CLIP_PLANE1);
			gl.glDisable(GL2.GL_CLIP_PLANE2);
			gl.glDisable(GL2.GL_CLIP_PLANE3);
		}
	}

	@Override
	public final Rectangle getClip() {
		return cliparea;
	}

	@Override
	public final ObjectHeap<ObjectHeap<GraphicsStorage>> loadedGraphics() {
		return textureManager.getDataMap();
	}

	@Override
	public final Component getCanvas() {
		return canvas;
	}
}
