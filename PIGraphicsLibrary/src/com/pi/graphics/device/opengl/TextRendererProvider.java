package com.pi.graphics.device.opengl;

import java.awt.Font;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GLContext;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.pi.common.debug.PILogger;
import com.pi.graphics.device.DeviceRegistration;

public class TextRendererProvider extends Thread {
	private Map<Font, TextRendererStorage> map = Collections
			.synchronizedMap(new HashMap<Font, TextRendererStorage>());
	private boolean running = true;
	private Object syncObject = new Object();
	private final DeviceRegistration dev;

	public TextRendererProvider(DeviceRegistration client) {
		super(client.getThreadGroup(), null, "TextRendererProvider");
		this.dev = client;
		super.start();
	}

	public TextRenderer fetchRenderer(Font f) {
		synchronized (syncObject) {
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

	public void dispose() {
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
			}
		}
		map.clear();
	}

	@Override
	public void run() {
		dev.getLog().fine("Started Text Renderer Provider");
		while (running) {
			synchronized (syncObject) {
				for (Font f : map.keySet()) {
					TextRendererStorage r = map.get(f);
					if (System.currentTimeMillis() - r.timeUsed > 30000) {// 30
						// seconds
						// TODO Actually Dispose
						try {
							GLContext.getCurrentGL();
							r.r.dispose();
						} catch (Exception e) {
						}
						map.remove(f);
					}
				}
			}
		}
		dev.getLog().fine("Killed Text Renderer Provider");
	}

	private static class TextRendererStorage {
		public TextRenderer r;
		public long timeUsed;

		@Override
		public boolean equals(Object o) {
			if (o instanceof TextRendererStorage) {
				return r.getFont()
						.equals(((TextRendererStorage) o).r.getFont());
			}
			return false;
		}
	}
}
