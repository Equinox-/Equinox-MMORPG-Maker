package com.pi.graphics.device.opengl;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.pi.common.game.ObjectHeap;
import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.GraphicsStorage;

/**
 * Class for loading textures using a threaded model.
 * 
 * @author Westin
 * 
 */
public class TextureManager extends Thread {
	/**
	 * The time in milliseconds for a texture to be disposed.
	 */
	private static final long TEXTURE_EXPIRY = 30000; // 30 seconds
	/**
	 * The graphics instance this manager is bound to.
	 */
	private GLGraphics glGraphics;
	/**
	 * The storage map.
	 */
	private volatile ObjectHeap<TextureStorage> map =
			new ObjectHeap<TextureStorage>();
	/**
	 * The texture loading queue.
	 */
	private Queue<Integer> loadQueue =
			new LinkedBlockingQueue<Integer>();
	/**
	 * Boolean monitoring the running state of this thread.
	 */
	private volatile boolean running = true;
	/**
	 * The registration object this manager is bound to.
	 */
	private final DeviceRegistration dev;

	/**
	 * Creates the texture manager for the specified graphics object and device
	 * registration.
	 * 
	 * @param gl the graphics object
	 * @param sDev the device registration
	 */
	public TextureManager(final GLGraphics gl,
			final DeviceRegistration sDev) {
		super(sDev.getThreadGroup(), null, "PiTextureManager");
		this.glGraphics = gl;
		this.dev = sDev;
		super.start();
	}

	/**
	 * Fetches the texture with the provided ID. Only works if the executing
	 * thread has a GLContext bound to it. Will also add the graphic to the
	 * loadQueue if it isn't loaded.
	 * 
	 * @param texID the texture id
	 * @return the texture object, or null if not loaded
	 */
	public final Texture fetchTexture(final int texID) {
		synchronized (map) {
			TextureStorage tS = map.get(texID);
			if (tS == null || tS.texData == null) {
				loadQueue.add(texID);
				return null;
			}
			tS.updateLastUsed();
			if (tS.texData != null && tS.tex == null) {
				tS.tex = TextureIO.newTexture(tS.texData);
			}
			map.set(texID, tS);
			return tS.tex;
		}
	}

	/**
	 * Class for storing the textures with their data.
	 * 
	 * @author Westin
	 * 
	 */
	private static class TextureStorage extends GraphicsStorage {
		/**
		 * The texture data backing the texture.
		 */
		private TextureData texData;
		/**
		 * The actual texture.
		 */
		private Texture tex;

		@Override
		public Object getGraphic() {
			return tex;
		}
	}

	/**
	 * Loads the texture data for the specified texture identification number.
	 * 
	 * @param oldestID the texture id
	 * @return the texture data, or <code>null</code> if not loaded
	 */
	private TextureData getTextureData(final Integer oldestID) {
		File tex = dev.getGraphicsFile(oldestID);
		if (tex == null) {
			return null;
		} else {
			try {
				int lastDot = tex.getName().lastIndexOf('.');
				if (lastDot < 0) {
					return null;
				}
				String suffix =
						tex.getName().substring(lastDot + 1);
				if (suffix == null) {
					return null;
				}
				return TextureIO.newTextureData(
						GLProfile.getDefault(), tex, false,
						suffix);
			} catch (Exception e) {
				dev.getLog().printStackTrace(e);
				return null;
			}
		}
	}

	@Override
	public final void run() {
		dev.getLog().fine("Started Texture Manager");
		while (running) {
			doRequest();
			removeExpired();
		}
		dev.getLog().fine("Killed Texture Manager");
	}

	/**
	 * Disposes the expired textures.
	 */
	private void removeExpired() {
		synchronized (map) {
			for (int i = 0; i < map.capacity(); i++) {
				if (map.get(i) != null) {
					if (System.currentTimeMillis()
							- map.get(i).getLastUsedTime() > TEXTURE_EXPIRY) {
						if (glGraphics.getCore() != null
								&& glGraphics.getCore().getGL() != null
								&& map.get(i).tex != null) {
							map.get(i).tex.destroy(glGraphics
									.getCore().getGL());
						}
						map.get(i).texData.flush();
						map.remove(i);
					}
				}
			}
		}
	}

	/**
	 * Process a request to load a texture.
	 */
	private void doRequest() {
		synchronized (map) {
			int oldestID = -1;
			if (loadQueue.size() > 0) {
				oldestID = loadQueue.poll();
			}
			if (oldestID != -1 && map.get(oldestID) == null) {
				loadQueue.remove(oldestID);
				TextureStorage tX = new TextureStorage();
				tX.texData = getTextureData(oldestID);
				tX.updateLastUsed();
				map.set(oldestID, tX);
			}
		}
	}

	/**
	 * Dispose this texture manager.
	 */
	public final void dispose() {
		running = false;
		try {
			super.join();
		} catch (InterruptedException e) {
			dev.getLog().printStackTrace(e);
			System.exit(0);
		}
	}

	/**
	 * Get the loaded graphics map.
	 * 
	 * @return the loaded map
	 */
	public final ObjectHeap<? extends GraphicsStorage> loadedMap() {
		return map;
	}
}
