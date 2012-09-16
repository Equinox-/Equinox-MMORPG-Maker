package com.pi.graphics.device;

import java.io.File;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pi.common.database.io.GraphicsDirectories;
import com.pi.common.util.ObjectHeap;

/**
 * Class for loading images into the ram using a threaded model.
 * 
 * @param <T> the type this image manager provides
 * @author Westin
 */
public abstract class ImageManager<T> extends Thread {
	/**
	 * The time it takes to unload an image from the ram.
	 */
	private static final long IMAGE_EXPIRY = 30000; // 30 seconds

	/**
	 * The object heap used to store the images.
	 */
	private ObjectHeap<ObjectHeap<GraphicsStorage>> map =
			new ObjectHeap<ObjectHeap<GraphicsStorage>>(
					GraphicsDirectories.values().length);
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
	@SuppressWarnings("unchecked")
	public T fetchImage(final int graphic) {
		synchronized (map) {
			if (!isRunning()) {
				return null;
			}
			ObjectHeap<GraphicsStorage> heap =
					map.get(graphic >>> GraphicsDirectories.DIRECTORY_BIT_SHIFT);
			if (heap == null) {
				heap = new ObjectHeap<GraphicsStorage>();
				map.set(graphic >>> GraphicsDirectories.DIRECTORY_BIT_SHIFT,
						heap);
			}
			GraphicsStorage tS =
					heap.get(graphic
							& GraphicsDirectories.FILE_MASK);
			if (tS == null) {
				loadQueue.add(graphic);
				map.notify();
				return null;
			}
			tS.updateLastUsed();
			heap.set(graphic & GraphicsDirectories.FILE_MASK, tS);
			return (T) tS.getGraphic();
		}
	}

	/**
	 * Load the image specified by the given file.
	 * 
	 * @param f the file
	 * @return the loaded image, or <code>null</code> if loading failed.
	 */
	protected abstract Object loadImage(final File f);

	@Override
	public final void run() {
		source.getLog().fine("Starting Image Manager Thread");
		while (isRunning()) {
			int oldestRequest = oldestRequest();
			if (oldestRequest != -1
					&& map.get(oldestRequest) == null) {
				GraphicsStorage tX = new GraphicsStorage();
				tX.setGraphic(loadImage(source
						.getGraphicsFile(
								oldestRequest >>> GraphicsDirectories.DIRECTORY_BIT_SHIFT,
								oldestRequest
										& GraphicsDirectories.FILE_MASK)));
				tX.updateLastUsed();
				ObjectHeap<GraphicsStorage> heap =
						map.get(oldestRequest >>> GraphicsDirectories.DIRECTORY_BIT_SHIFT);
				if (heap == null) {
					heap = new ObjectHeap<GraphicsStorage>();
					map.set(oldestRequest >>> GraphicsDirectories.DIRECTORY_BIT_SHIFT,
							heap);
				}
				heap.set(oldestRequest
						& GraphicsDirectories.FILE_MASK, tX);
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
			for (int dir = 0; dir < map.capacity(); dir++) {
				ObjectHeap<GraphicsStorage> stor = map.get(dir);
				if (stor != null) {
					for (int i = 0; i < stor.capacity(); i++) {
						if (stor.get(i) != null) {
							if (System.currentTimeMillis()
									- stor.get(i)
											.getLastUsedTime() > IMAGE_EXPIRY) {
								GraphicsStorage str =
										stor.remove(i);
								if (str != null) {
									dispose(str);
								}
							}
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
		for (int dir = 0; dir < map.capacity(); dir++) {
			ObjectHeap<GraphicsStorage> heap = map.remove(dir);
			if (heap != null) {
				for (int i = 0; i < heap.capacity(); i++) {
					GraphicsStorage str = heap.remove(i);
					if (str != null) {
						dispose(str);
					}
				}
			}
		}
	}

	/**
	 * Disposes of the given graphics resource.
	 * 
	 * @param obj the resource to dispose
	 */
	protected abstract void dispose(GraphicsStorage obj);

	/**
	 * Gets the object heap controlled by this image manager.
	 * 
	 * @return the storage heap
	 */
	public final ObjectHeap<ObjectHeap<GraphicsStorage>> getDataMap() {
		return map;
	}

	/**
	 * Gets the running state of this image manager.
	 * 
	 * @return the running state
	 */
	public final boolean isRunning() {
		return running;
	}

	/**
	 * Adds the provided ID to the load queue.
	 * 
	 * @param id the ID to be added
	 */
	protected final void addToLoadQueue(final int id) {
		loadQueue.add(id);
	}

	/**
	 * Gets the device this image manager is registered to.
	 * 
	 * @return the device
	 */
	protected final DeviceRegistration getDeviceRegistration() {
		return source;
	}
}
