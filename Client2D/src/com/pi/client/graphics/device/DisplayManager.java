package com.pi.client.graphics.device;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import com.pi.client.Client;
import com.pi.client.graphics.RenderLoop;
import com.pi.client.graphics.device.awt.AWTGraphics;
import com.pi.client.graphics.device.opengl.GLGraphics;

public class DisplayManager {
    private boolean hasOpenGL = false;
    private IGraphics graphics;
    private RenderLoop renderLoop;
    private Client client;
    private boolean showFps = true;

    public Map<String, ? extends GraphicsStorage> loadedGraphics() {
	return graphics != null ? graphics.loadedGraphics()
		: new HashMap<String, GraphicsStorage>();
    }

    public boolean hasOpenGL() {
	return hasOpenGL;
    }

    public void postInititation() {
	try {
	    client.getLog().fine("Checking for opengl...");
	    new GLCapabilities(GLProfile.getDefault());
	    hasOpenGL = true;
	} catch (Exception e) {
	    e.printStackTrace(client.getLog().getErrorStream());
	}
	client.getLog().fine(
		"We " + (hasOpenGL ? "" : "don't ") + "have OpenGL");
	renderLoop.postInitiation();
    }

    public DisplayManager(Client client) {
	this.client = client;
	this.renderLoop = new RenderLoop(client);
	setMode(GraphicsMode.AWT);
    }

    public Component getListenerRegistration() {
	if (graphics != null && getMode().equals(GraphicsMode.OpenGL)) {
	    return ((GLGraphics) graphics).canvas;
	} else {
	    return client.getApplet();
	}
    }

    public Client getClient() {
	return client;
    }

    public void setMode(GraphicsMode m) {
	try {
	    if (graphics != null)
		graphics.dispose();
	    graphics = m.getClazz().getConstructor(DisplayManager.class)
		    .newInstance(this);
	} catch (Exception e) {
	    client.fatalError("Failed to switch to " + m.name());
	    e.printStackTrace(client.getLog().getErrorStream());
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

    public RenderLoop getRenderLoop() {
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
