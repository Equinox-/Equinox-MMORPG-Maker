package com.pi.graphics.device.awt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;

import com.pi.common.game.ObjectHeap;
import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.GraphicsStorage;

/**
 * Class for loading images into the ram using a threaded model.
 * 
 * @author Westin
 * 
 */
public class ImageManager extends Thread {
	/**
	 * The time it takes to unload an image from the ram.
	 */
	private static final long IMAGE_EXPIRY = 30000; // 30 seconds

	/**
	 * The object heap used to store the images.
	 */
	private ObjectHeap<ImageStorage> map =
			new ObjectHeap<ImageStorage>();
	/**
	 * The image load queue.
	 */
	private Queue<Integer> loadQueue =
			new LinkedBlockingQueue<Integer>();
	/**
	 * Boolean representing the running state of this thread.
	 */
	private volatile boolean running = true;
	/**
	 * The object this manager is registered to.
	 */
	private final DeviceRegistration source;

	/**
	 * Create an image manager linked to the specified object.
	 * 
	 * @param device the device to link to
	 */
	public ImageManager(final DeviceRegistration device) {
		super(device.getThreadGroup(), null, "PiImageManager");
		this.source = device;
		super.start();
	}

	/**
	 * Get the image with the specified graphical identification number.
	 * 
	 * @param graphic the graphics id
	 * @return the image if it's loaded, or <code>null</code> if not loaded
	 */
	public final BufferedImage fetchImage(final int graphic) {
		synchronized (map) {
			if (!running) {
				return null;
			}
			ImageStorage tS = map.get(graphic);
			if (tS == null) {
				loadQueue.add(graphic);
				map.notify();
				return null;
			}
			tS.updateLastUsed();
			map.set(graphic, tS);
			return tS.img;
		}
	}

	/**
	 * Class for storing the images once they are loaded.
	 * 
	 * @author Westin
	 * 
	 */
	public static class ImageStorage extends GraphicsStorage {
		/**
		 * The image bound to this storage object.
		 */
		private BufferedImage img;

		@Override
		public final Object getGraphic() {
			return img;
		}
	}

	/**
	 * Load the image specified by the given graphical identification number.
	 * 
	 * @param id the graphics id
	 * @return the loaded image, or <code>null</code> if loading failed.
	 */
	private synchronized BufferedImage loadImage(final int id) {
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
	public final void run() {
		source.getLog().fine("Starting Image Manager Thread");
		while (running) {
			int oldestRequest = oldestRequest();
			if (oldestRequest != -1
					&& map.get(oldestRequest) == null) {
				ImageStorage tX = new ImageStorage();
				tX.img = loadImage(oldestRequest);
				tX.updateLastUsed();
				map.set(oldestRequest, tX);
			} else {
				synchronized (map) {
					try {
						map.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			removeExpired();
		}
		source.getLog().fine("Killing Image Manager Thread");
	}

	/**
	 * Remove the expired images from the RAM.
	 */
	private void removeExpired() {
		synchronized (map) {
			for (int i = 0; i < map.capacity(); i++) {
				if (map.get(i) != null) {
					if (System.currentTimeMillis()
							- map.get(i).getLastUsedTime() > IMAGE_EXPIRY) {
						ImageStorage str = map.remove(i);
						if (str != null && str.img != null) {
							str.img.flush();
						}
					}
				}
			}
		}
	}

	/**
	 * Get the oldest requested image, or <code>-1</code> if there isn't one.
	 * 
	 * @return the oldest request
	 */
	private int oldestRequest() {
		synchronized (map) {
			if (loadQueue.size() > 0) {
				return loadQueue.poll();
			}
			return -1;
		}
	}

	/**
	 * Disposes this image manager.
	 */
	public final void dispose() {
		running = false;
		try {
			synchronized (map) {
				map.notify();
			}
			super.join();
		} catch (InterruptedException e) {
			source.getLog().printStackTrace(e);
			System.exit(0);
		}
		loadQueue.clear();
		for (int i = 0; i < map.capacity(); i++) {
			ImageStorage str = map.remove(i);
			if (str != null && str.img != null) {
				str.img.flush();
			}
		}
	}

	/**
	 * Gets the object heap controlled by this image manager.
	 * 
	 * @return the storage heap
	 */
	public final ObjectHeap<ImageStorage> loadedMap() {
		return map;
	}
}
