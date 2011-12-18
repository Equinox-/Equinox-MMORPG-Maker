package com.pi.client.graphics.device.awt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.pi.client.Client;
import com.pi.client.database.Paths;
import com.pi.client.graphics.device.GraphicsStorage;
import com.pi.client.graphics.device.awt.ImageManager.ImageStorage;
import com.pi.common.game.ObjectHeap;

public class ImageManager extends Thread {
    private static long imageExpiry = 30000; // 30 seconds
    private ObjectHeap<ImageStorage> map = new ObjectHeap<ImageStorage>();
    private Vector<Integer> loadQueue = new Vector<Integer>();
    /*
     * private Map<Integer, Long> loadQueue = Collections .synchronizedMap(new
     * HashMap<Integer, Long>());
     */
    private boolean running = true;
    private final Client client;

    public ImageManager(Client client) {
	super(client.getThreadGroup(), null, "PiImageManager");
	this.client = client;
	super.start();
    }

    public BufferedImage fetchImage(int graphic) {
	synchronized (map) {
	    if (!running)
		return null;
	    ImageStorage tS = map.get(graphic);
	    if (tS == null) {
		// loadQueue.put(graphic, System.currentTimeMillis());
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
	File img = Paths.getGraphicsFile(id);
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
	client.getLog().fine("Starting Image Manager Thread");
	while (running) {
	    int oldestRequest = oldestRequest();
	    if (oldestRequest != -1) {
		ImageStorage tX = new ImageStorage();
		tX.img = loadImage(oldestRequest);
		tX.lastUsed = System.currentTimeMillis();
		map.set(oldestRequest, tX);
	    }
	    removeExpired();
	}
	client.getLog().fine("Killing Image Manager Thread");
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
	    /*
	     * long oldestTime = Long.MAX_VALUE; int oldestID = -1; for (Integer
	     * i : loadQueue.keySet()) { long requestTime = loadQueue.get(i); if
	     * (System.currentTimeMillis() - requestTime > imageExpiry) {
	     * loadQueue.remove(i); } else { if (oldestTime > requestTime) {
	     * oldestTime = requestTime; oldestID = i; } } } if (oldestID != -1)
	     * loadQueue.remove(oldestID); return oldestID;
	     */
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
	    e.printStackTrace();
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
