package com.pi.graphics.device;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import com.pi.common.game.ObjectHeap;
import com.pi.graphics.device.awt.AWTGraphics;
import com.pi.graphics.device.opengl.GLGraphics;

public class DisplayManager {
    private boolean hasOpenGL = false;
    private IGraphics graphics;
    private Renderable renderLoop;
    private DeviceRegistration dev;
    private boolean showFps = true;
    public final int maxFPS = 50; // -1 will not limit
    public final long minMSPerFrame = 1000 / maxFPS;

    public ObjectHeap<? extends GraphicsStorage> loadedGraphics() {
	return graphics != null ? graphics.loadedGraphics() : null;
    }

    public boolean hasOpenGL() {
	return hasOpenGL;
    }

    public void postInititation() {
	/*
	 * try { client.getLog().info("Checking for opengl..."); new
	 * GLCapabilities(GLProfile.getDefault()); hasOpenGL = true; } catch
	 * (Exception e) { client.getLog().printStackTrace(e); }
	 * client.getLog().info( "We " + (hasOpenGL ? "" : "don't ") +
	 * "have OpenGL");
	 */
    }

    public DisplayManager(DeviceRegistration reg, Renderable renderLoop) {
	this.dev = reg;
	this.renderLoop = renderLoop;
	setMode(GraphicsMode.AWT);
    }

    public Component getListenerRegistration() {
	if (graphics != null && getMode().equals(GraphicsMode.OpenGL)) {
	    return ((GLGraphics) graphics).canvas;
	} else {
	    return dev.getContainer();
	}
    }

    public DeviceRegistration getSource() {
	return dev;
    }

    public void setMode(GraphicsMode m) {
	try {
	    // We also need to re-register all the listeners... *sigh*
	    MouseListener[] mouse = null;
	    MouseMotionListener[] mouseM = null;
	    MouseWheelListener[] mouseW = null;
	    KeyListener[] key = null;
	    if (graphics != null) {

		key = getListenerRegistration().getKeyListeners().clone();
		mouseW = getListenerRegistration().getMouseWheelListeners()
			.clone();
		mouseM = getListenerRegistration().getMouseMotionListeners()
			.clone();
		mouse = getListenerRegistration().getMouseListeners().clone();
		for (MouseListener l : mouse)
		    getListenerRegistration().removeMouseListener(l);
		for (MouseWheelListener l : mouseW)
		    getListenerRegistration().removeMouseWheelListener(l);
		for (MouseMotionListener l : mouseM)
		    getListenerRegistration().removeMouseMotionListener(l);
		for (KeyListener l : key)
		    getListenerRegistration().removeKeyListener(l);

		graphics.dispose();
	    }
	    graphics = m.getClazz().getConstructor(DisplayManager.class)
		    .newInstance(this);

	    if (mouse != null)
		for (MouseListener l : mouse)
		    getListenerRegistration().addMouseListener(l);
	    if (mouseW != null)
		for (MouseWheelListener l : mouseW)
		    getListenerRegistration().addMouseWheelListener(l);
	    if (mouseM != null)
		for (MouseMotionListener l : mouseM)
		    getListenerRegistration().addMouseMotionListener(l);
	    if (key != null)
		for (KeyListener l : key)
		    getListenerRegistration().addKeyListener(l);

	    getListenerRegistration().setFocusTraversalKeysEnabled(false);
	} catch (Exception e) {
	    dev.fatalError("Failed to switch to " + m.name());
	    dev.getLog().printStackTrace(e);
	}
    }

    public GraphicsMode getMode() {
	for (GraphicsMode m : GraphicsMode.values()) {
	    if (graphics.getClass().getCanonicalName()
		    .equals(m.getClazz().getCanonicalName()))
		return m;
	}
	return null;
    }

    public void doRender() {
	renderLoop.render(graphics);
	if (showFps) {
	    graphics.drawText("Fps: " + graphics.getFPS(), 10, 10,
		    Font.decode(Font.SERIF).deriveFont(10f), Color.GREEN);
	}
    }

    public void dispose() {
	if (graphics != null) {
	    graphics.dispose();
	}
    }

    public Renderable getRenderLoop() {
	return renderLoop;
    }

    public static enum GraphicsMode {
	OpenGL(GLGraphics.class), AWT(AWTGraphics.class);
	private final Class<? extends IGraphics> clazz;

	private GraphicsMode(Class<? extends IGraphics> clazz) {
	    this.clazz = clazz;
	}

	public Class<? extends IGraphics> getClazz() {
	    return clazz;
	}
    }
}
