package com.pi.client.graphics.device.opengl;

import java.io.File;
import java.util.Vector;

import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.pi.client.Client;
import com.pi.client.database.Paths;
import com.pi.client.graphics.device.GraphicsStorage;
import com.pi.common.game.ObjectHeap;

public class TextureManager extends Thread {
    private static long textureExpiry = 30000; // 30 seconds
    private GLGraphics glGraphics;
    private ObjectHeap<TextureStorage> map = new ObjectHeap<TextureStorage>();
    private Vector<Integer> loadQueue = new Vector<Integer>();
    private boolean running = true;
    private final Client client;
    private Object syncObject = new Object();

    public TextureManager(GLGraphics gl, Client client) {
	super(client.getThreadGroup(), null, "PiTextureManager");
	this.glGraphics = gl;
	this.client = client;
	super.start();
    }

    /**
     * Fetches the texture with the provided ID. Only works if the executing
     * thread has a GLContext bound to it. Will also add the graphic to the
     * loadQueue if it isn't loaded.
     * 
     * @param texID
     * @return the texture object, or null if not loaded
     */
    public Texture fetchTexture(int texID) {
	synchronized (syncObject) {
	    TextureStorage tS = map.get(texID);
	    if (tS == null || tS.texData == null) {
		loadQueue.add(texID);
		return null;
	    }
	    tS.lastUsed = System.currentTimeMillis();
	    if (tS.texData != null && tS.tex == null)
		tS.tex = TextureIO.newTexture(tS.texData);
	    map.set(texID, tS);
	    return tS.tex;
	}
    }

    private static class TextureStorage extends GraphicsStorage {
	private TextureData texData;
	public Texture tex;

	@Override
	public Object getGraphic() {
	    return tex;
	}
    }

    private TextureData getTextureData(Integer oldestID) {
	File tex = Paths.getGraphicsFile(oldestID);
	if (tex == null) {
	    return null;
	} else {
	    try {
		int lastDot = tex.getName().lastIndexOf('.');
		if (lastDot < 0) {
		    return null;
		}
		String suffix = tex.getName().substring(lastDot + 1);
		if (suffix == null)
		    return null;
		return TextureIO.newTextureData(GLProfile.getDefault(), tex,
			false, suffix);
	    } catch (Exception e) {
		client.getLog().printStackTrace(e);
		return null;
	    }
	}
    }

    @Override
    public void run() {
	client.getLog().fine("Started Texture Manager");
	while (running) {
	    doRequest();
	    removeExpired();
	}
	client.getLog().fine("Killed Texture Manager");
    }

    private void removeExpired() {
	synchronized (syncObject) {
	    for (int i = 0; i < map.size(); i++) {
		if (map.get(i) != null) {
		    if (System.currentTimeMillis() - map.get(i).lastUsed > textureExpiry) {
			if (glGraphics.getCore() != null
				&& glGraphics.getCore().getGL() != null
				&& map.get(i).tex != null) {
			    map.get(i).tex
				    .destroy(glGraphics.getCore().getGL());
			}
			map.get(i).texData.flush();
			map.remove(i);
		    }
		}
	    }
	}
    }

    private void doRequest() {
	synchronized (syncObject) {
	    int oldestID = loadQueue.size() > 0 ? loadQueue.remove(0) : -1;
	    if (oldestID != -1 && map.get(oldestID) == null) {
		loadQueue.remove(oldestID);
		TextureStorage tX = new TextureStorage();
		tX.texData = getTextureData(oldestID);
		tX.lastUsed = System.currentTimeMillis();
		map.set(oldestID, tX);
	    }
	}
    }

    public void dispose() {
	running = false;
	try {
	    super.join();
	} catch (InterruptedException e) {
	    client.getLog().printStackTrace(e);
	    System.exit(0);
	}
    }

    public ObjectHeap<? extends GraphicsStorage> loadedMap() {
	return map;
    }
}
