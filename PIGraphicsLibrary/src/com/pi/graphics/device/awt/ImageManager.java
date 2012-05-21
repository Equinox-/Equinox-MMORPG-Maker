package com.pi.graphics.device.awt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.pi.common.game.ObjectHeap;
import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.GraphicsStorage;

public class ImageManager extends Thread {
	private static long imageExpiry = 30000; // 30 seconds
	private ObjectHeap<ImageStorage> map = new ObjectHeap<ImageStorage>();
	private Vector<Integer> loadQueue = new Vector<Integer>();
	private boolean running = true;
	private final DeviceRegistration source;

	public ImageManager(DeviceRegistration device) {
		super(device.getThreadGroup(), null, "PiImageManager");
		this.source = device;
		super.start();
	}

	public BufferedImage fetchImage(int graphic) {
		synchronized (map) {
			if (!running)
				return null;
			ImageStorage tS = map.get(graphic);
			if (tS == null) {
				loadQueue.add(graphic);
				return null;
			}
			tS.lastUsed = System.currentTimeMillis();
			map.set(graphic, tS);
			return tS.img;
		}
	}

	public static class ImageStorage extends GraphicsStorage {
		public BufferedImage img;

		@Override
		public Object getGraphic() {
			return img;
		}
	}

	private synchronized BufferedImage loadImage(Integer id) {
		File img = source.getGraphicsFile(id);
		if (img == null) {
			return null;
		} else {
			try {
				return ImageIO.read(img);
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	public void run() {
		source.getLog().fine("Starting Image Manager Thread");
		while (running) {
			int oldestRequest = oldestRequest();
			if (oldestRequest != -1 && map.get(oldestRequest) == null) {
				ImageStorage tX = new ImageStorage();
				tX.img = loadImage(oldestRequest);
				tX.lastUsed = System.currentTimeMillis();
				map.set(oldestRequest, tX);
			}
			removeExpired();
		}
		source.getLog().fine("Killing Image Manager Thread");
	}

	private void removeExpired() {
		for (int i = 0; i < map.capacity(); i++) {
			if (map.get(i) != null) {
				if (System.currentTimeMillis() - map.get(i).lastUsed > imageExpiry) {
					ImageStorage str = map.remove(i);
					if (str != null && str.img != null)
						str.img.flush();
				}
			}
		}
	}

	private int oldestRequest() {
		synchronized (map) {
			if (loadQueue.size() > 0)
				return loadQueue.remove(0);
			return -1;
		}
	}

	public void dispose() {
		running = false;
		try {
			super.join();
		} catch (InterruptedException e) {
			source.getLog().printStackTrace(e);
			System.exit(0);
		}
		loadQueue.clear();
		for (int i = 0; i < map.capacity(); i++) {
			ImageStorage str = map.remove(i);
			if (str != null && str.img != null)
				str.img.flush();
		}
	}

	public ObjectHeap<ImageStorage> loadedMap() {
		return map;
	}
}
