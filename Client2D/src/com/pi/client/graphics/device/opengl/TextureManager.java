package com.pi.client.graphics.device.opengl;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.pi.client.Client;
import com.pi.client.database.Paths;
import com.pi.client.graphics.device.GraphicsStorage;

public class TextureManager extends Thread {
	private static long textureExpiry = 30000; // 30 seconds
	private GLGraphics glGraphics;
	private Map<String, TextureStorage> map = new HashMap<String, TextureStorage>();
	private Map<String, Long> loadQueue = new HashMap<String, Long>();
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
	 * @param id
	 * @return the texture object, or null if not loaded
	 */
	public Texture fetchTexture(String id) {
		synchronized (syncObject) {
			TextureStorage tS = map.get(id);
			if (tS == null || tS.texData == null) {
				loadQueue.put(id, System.currentTimeMillis());
				return null;
			}
			tS.lastUsed = System.currentTimeMillis();
			if (tS.texData != null && tS.tex == null)
				tS.tex = TextureIO.newTexture(tS.texData);
			map.put(id, tS);
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

	private TextureData getTextureData(String id) {
		File tex = Paths.getGraphicsFile(id);
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
				e.printStackTrace(client.getLog().getErrorStream());
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
			for (String i : map.keySet()) {
				if (System.currentTimeMillis() - map.get(i).lastUsed > textureExpiry) {
					if (glGraphics.getCore() != null
							&& glGraphics.getCore().getGL() != null
							&& map.get(i).tex != null) {
						map.get(i).tex.destroy(glGraphics.getCore().getGL());
					}
					map.get(i).texData.flush();
					map.remove(i);
				}
			}
		}
	}

	private void doRequest() {
		synchronized (syncObject) {
			long oldestTime = Long.MAX_VALUE;
			String oldestID = null;
			for (String i : loadQueue.keySet()) {
				long requestTime = loadQueue.get(i);
				if (System.currentTimeMillis() - requestTime > textureExpiry) {
					loadQueue.remove(i);
				} else {
					if (oldestTime > requestTime && i != null) {
						oldestTime = requestTime;
						oldestID = i;
					}
				}
			}
			if (oldestID != null) {
				loadQueue.remove(oldestID);
				TextureStorage tX = new TextureStorage();
				tX.texData = getTextureData(oldestID);
				tX.lastUsed = System.currentTimeMillis();
				map.put(oldestID, tX);
			}
		}
	}

	public void dispose() {
		running = false;
		try {
			super.join();
		} catch (InterruptedException e) {
			e.printStackTrace(client.getLog().getErrorStream());
			System.exit(0);
		}
	}

	public Map<String, TextureStorage> loadedMap() {
		return Collections.unmodifiableMap(map);
	}
}
