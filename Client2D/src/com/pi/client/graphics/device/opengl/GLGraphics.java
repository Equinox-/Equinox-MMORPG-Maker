package com.pi.client.graphics.device.opengl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.pi.client.graphics.device.DisplayManager;
import com.pi.client.graphics.device.GraphicsHeap;
import com.pi.client.graphics.device.GraphicsStorage;
import com.pi.client.graphics.device.IGraphics;

public class GLGraphics extends IGraphics implements GLEventListener {
    private final Animator animator;
    private final TextRendererProvider txtRender;
    private final TextureManager textureManager;
    private Rectangle cliparea;

    public GLGraphics(DisplayManager mgr) {
	super(mgr);
	GLProfile.initSingleton(false);
	canvas = new GLCanvas();
	super.mgr.getClient().getApplet().add(canvas);
	canvas.addGLEventListener(this);
	canvas.setSize(super.mgr.getClient().getApplet().getSize());
	cliparea = canvas.getBounds();
	canvas.setLocation(0, 0);
	animator = new Animator(mgr.getClient().getThreadGroup());
	animator.setRunAsFastAsPossible(true);
	animator.add(canvas);
	animator.start();
	mgr.getClient().getLog().fine("Started graphics thread");
	txtRender = new TextRendererProvider(mgr.getClient());
	textureManager = new TextureManager(this, mgr.getClient());
    }

    private GL2 gl;
    private GLAutoDrawable glCore;
    public final GLCanvas canvas;

    public GLAutoDrawable getCore() {
	return glCore;
    }

    @Override
    public void drawLine(int x1, int y1, Color aC, int x2, int y2, Color bC) {
	gl.glBegin(GL.GL_LINES);
	gl.glColor4i(aC.getRed(), aC.getGreen(), aC.getBlue(), aC.getAlpha());
	gl.glVertex2f(x1, y1);
	gl.glColor4i(bC.getRed(), bC.getGreen(), bC.getBlue(), bC.getAlpha());
	gl.glVertex2f(x2, y2);
	gl.glEnd();
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
	gl.glBegin(GL.GL_LINES);
	gl.glVertex2f(x1, y1);
	gl.glVertex2f(x2, y2);
	gl.glEnd();
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
	gl.glBegin(GL2.GL_LINE_LOOP);
	gl.glVertex2f(x, y);
	gl.glVertex2f(x + width, y);
	gl.glVertex2f(x + width, (y + height));
	gl.glVertex2f(x, (y + height));
	gl.glEnd();
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
	gl.glBegin(GL2.GL_QUADS);
	gl.glVertex2f(x, y);
	gl.glVertex2f(x + width, y);
	gl.glVertex2f(x + width, (y + height));
	gl.glVertex2f(x, (y + height));
	gl.glEnd();
    }

    @Override
    public void drawImage(int texID, int dx, int dy, int dwidth,
	    int dheight, int sx, int sy, int swidth, int sheight) {
	Texture tex = textureManager.fetchTexture(texID);
	if (tex != null) {
	    double width = tex.getWidth();
	    double height = tex.getHeight();
	    tex.enable();
	    tex.bind();
	    setColor(Color.WHITE);
	    gl.glBegin(GL2.GL_QUADS);
	    gl.glTexCoord2d(sx / width, sy / height);
	    gl.glVertex2f(dx, dy);
	    gl.glTexCoord2d((sx + swidth) / width, sy / height);
	    gl.glVertex2f(dx + dwidth, dy);
	    gl.glTexCoord2d((sx + swidth) / width, (sy + sheight) / height);
	    gl.glVertex2f(dx + dwidth, (dy + dheight));
	    gl.glTexCoord2d(sx / width, (sy + sheight) / height);
	    gl.glVertex2f(dx, (dy + dheight));
	    gl.glEnd();
	    tex.disable();
	}
    }

    @Override
    public void drawPoint(int x, int y) {
	gl.glBegin(GL.GL_POINTS);
	gl.glVertex2f(x, y);
	gl.glEnd();
    }

    @Override
    public void setColor(Color color) {
	gl.glColor4f(color.getRed() / 255f, color.getGreen() / 255f,
		color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    @Override
    public void display(GLAutoDrawable arg0) {
	    if (!canvas.getSize().equals(
		    super.mgr.getClient().getApplet().getSize())) {
		canvas.setSize(super.mgr.getClient().getApplet().getSize());
		cliparea = canvas.getBounds();
		canvas.reshape(0, 0, super.mgr.getClient().getApplet()
			.getWidth(), super.mgr.getClient().getApplet()
			.getHeight());
	    }
	    this.gl = arg0.getGL().getGL2();
	    this.glCore = arg0;
	    gl.glMatrixMode(GL2.GL_PROJECTION);
	    gl.glLoadIdentity();
	    gl.glOrtho(0, canvas.getWidth(), canvas.getHeight(), 0, 1, -1);
	    gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	    super.doRender();
	}


    @Override
    public void dispose(GLAutoDrawable arg0) {
	this.gl = arg0.getGL().getGL2();
	this.glCore = arg0;
    }

    @Override
    public void init(GLAutoDrawable arg0) {
	this.gl = arg0.getGL().getGL2();
	this.glCore = arg0;
	this.gl.setSwapInterval(1);
	this.gl.glEnable(GL.GL_BLEND);
	this.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
	    int arg4) {
	this.gl = arg0.getGL().getGL2();
	this.glCore = arg0;
    }

    @Override
    public void dispose() {
	animator.stop();
	mgr.getClient().getLog().fine("Killed OpenGL Graphics thread");
	txtRender.dispose();
	textureManager.dispose();
	if (canvas != null)
	    canvas.destroy();
    }

    @Override
    public void drawText(String text, int x, int y, Font f, Color color) {
	TextRenderer tRender = txtRender.fetchRenderer(f);
	tRender.beginRendering(canvas.getWidth(), canvas.getHeight());
	tRender.setColor(color);
	tRender.draw(text, x,
		canvas.getHeight() - y
			- (int) tRender.getBounds(text).getHeight());
	tRender.endRendering();
    }

    @Override
    public Rectangle2D getStringBounds(Font f, String s) {
	return txtRender.fetchRenderer(f).getBounds(s);
    }

    @Override
    public int getFPS() {
	long duration = animator.getDuration() / 1000;
	if (duration != 0) {
	    return (int) (animator.getTotalFrames() / duration);
	}
	return 0;
    }

    @Override
    public int getImageWidth(int graphic) {
	Texture tex = textureManager.fetchTexture(graphic);
	if (tex != null)
	    return tex.getWidth();
	return 0;
    }

    @Override
    public int getImageHeight(int graphic) {
	Texture tex = textureManager.fetchTexture(graphic);
	if (tex != null)
	    return tex.getHeight();
	return 0;
    }

    @Override
    public void setClip(Rectangle r) {
	if (r != null) {
	    gl.glClipPlane(GL2.GL_CLIP_PLANE0, new double[] { 1, 0, 0, -r.x },
		    0);
	    gl.glClipPlane(GL2.GL_CLIP_PLANE1, new double[] { 0, 1, 0, -r.y },
		    0);
	    gl.glClipPlane(GL2.GL_CLIP_PLANE2, new double[] { -1, 0, 0,
		    r.x + r.width }, 0);
	    gl.glClipPlane(GL2.GL_CLIP_PLANE3, new double[] { 0, -1, 0,
		    r.y + r.height }, 0);
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
    public Rectangle getClip() {
	return cliparea;
    }

    @Override
    public GraphicsHeap<? extends GraphicsStorage> loadedGraphics() {
	return textureManager.loadedMap();
    }
}
