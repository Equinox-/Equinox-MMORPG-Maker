package com.pi.graphics.device.opengl;

import java.awt.Font;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GLContext;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.pi.common.debug.PILogger;
import com.pi.graphics.device.DeviceRegistration;

/**
 * Class for providing text renderer instances for the OpenGL graphics object.
 * 
 * @author Westin
 * 
 */
public class TextRendererProvider extends Thread {
	/**
	 * The time in milliseconds before an unused text renderer is disposed.
	 */
	private static final long TEXT_RENDERER_EXPIRY_TIME = 30000;
	/**
	 * The map linking a specific font to the given storage object.
	 */
	private volatile Map<Font, TextRendererStorage> map =
			Collections
					.synchronizedMap(new HashMap<Font, TextRendererStorage>());
	/**
	 * The boolean monitoring the state of the thread.
	 */
	private volatile boolean running = true;
	/**
	 * The registration object this provider is bound to.
	 */
	private final DeviceRegistration dev;

	/**
	 * Create a text renderer provider for the given registration object.
	 * 
	 * @param sDev the device registration object.
	 */
	public TextRendererProvider(final DeviceRegistration sDev) {
		super(sDev.getThreadGroup(), null,
				"TextRendererProvider");
		this.dev = sDev;
		super.start();
	}

	/**
	 * Gets the text rendering object bound to the specified font.
	 * 
	 * @param f the font to get a rendering object for
	 * @return the text render to be used with this client
	 */
	public final TextRenderer fetchRenderer(final Font f) {
		synchronized (map) {
			TextRendererStorage render = map.get(f);
			if (render == null) {
				render = new TextRendererStorage();
				render.r = new TextRenderer(f);
			}
			render.timeUsed = System.currentTimeMillis();
			map.put(f, render);
			return render.r;
		}
	}

	/**
	 * Disposes this text renderer provider and releases all resources.
	 */
	public final void dispose() {
		running = false;
		try {
			join();
		} catch (InterruptedException e) {
			dev.fatalError(PILogger.exceptionToString(e));
		}
		for (TextRendererStorage r : map.values()) {
			try {
				GLContext.getCurrentGL();
				r.r.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		map.clear();
	}

	@Override
	public final void run() {
		dev.getLog().fine("Started Text Renderer Provider");
		while (running) {
			synchronized (map) {
				for (Font f : map.keySet()) {
					TextRendererStorage r = map.get(f);
					if (System.currentTimeMillis() - r.timeUsed > TEXT_RENDERER_EXPIRY_TIME) {
						try {
							GLContext.getCurrentGL();
							r.r.dispose();
						} catch (Exception e) {
							e.printStackTrace();
						}
						map.remove(f);
					}
				}
			}
		}
		dev.getLog().fine("Killed Text Renderer Provider");
	}

	/**
	 * Wrapper class for storing a rendering object.
	 * 
	 * @author Westin
	 * 
	 */
	private static class TextRendererStorage {
		/**
		 * The bound text renderer object.
		 */
		private TextRenderer r;
		/**
		 * The last time used.
		 */
		private long timeUsed;

		@Override
		public int hashCode() {
			return r.getFont().hashCode();
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof TextRendererStorage) {
				return r.getFont().equals(
						((TextRendererStorage) o).r.getFont());
			}
			return false;
		}
	}
}
