package com.pi.client.graphics.device.awt;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import com.pi.client.Client;
import com.pi.client.database.Paths;
import com.pi.client.graphics.device.GraphicsStorage;

public class ImageManager extends Thread {
    private static long imageExpiry = 30000; // 30 seconds
    private Map<String, ImageStorage> map = Collections
	    .synchronizedMap(new HashMap<String, ImageStorage>());
    private Map<String, Long> loadQueue = Collections
	    .synchronizedMap(new HashMap<String, Long>());
    private boolean running = true;
    private final Client client;

    public ImageManager(Client client) {
	super(client.getThreadGroup(), null, "PiImageManager");
	this.client = client;
	super.start();
    }

    public BufferedImage fetchImage(String id) {
	synchronized (map) {
	    if (!running)
		return null;
	    ImageStorage tS = map.get(id);
	    if (tS == null) {
		loadQueue.put(id, System.currentTimeMillis());
		return null;
	    }
	    tS.lastUsed = System.currentTimeMillis();
	    map.put(id, tS);
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

    private synchronized BufferedImage loadImage(String id) {
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
	    synchronized (map) {
		String oldestRequest = oldestRequest();
		if (oldestRequest != null) {
		    ImageStorage tX = new ImageStorage();
		    tX.img = loadImage(oldestRequest);
		    tX.lastUsed = System.currentTimeMillis();
		    map.put(oldestRequest, tX);
		}
	    }
	    removeExpired();
	}
	client.getLog().fine("Killing Image Manager Thread");
    }

    private void removeExpired() {
	synchronized (map) {
	    for (String i : map.keySet()) {
		if (System.currentTimeMillis() - map.get(i).lastUsed > imageExpiry) {
		    ImageStorage str = map.remove(i);
		    if (str != null && str.img != null)
			str.img.flush();
		}
	    }
	}
    }

    private String oldestRequest() {
	synchronized (map) {
	    long oldestTime = Long.MAX_VALUE;
	    String oldestID = null;
	    for (String i : loadQueue.keySet()) {
		long requestTime = loadQueue.get(i);
		if (System.currentTimeMillis() - requestTime > imageExpiry) {
		    loadQueue.remove(i);
		} else {
		    if (oldestTime > requestTime) {
			oldestTime = requestTime;
			oldestID = i;
		    }
		}
	    }
	    if (oldestID != null)
		loadQueue.remove(oldestID);
	    return oldestID;
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
	/*
	 * synchronized (map) { for (String s : map.keySet()) {
	 * synchronized (map) { ImageStorage str = map.remove(s); if (str
	 * != null && str.img != null) str.img.flush(); } } }
	 */
    }

    public Map<String, ImageStorage> loadedMap() {
	return Collections.unmodifiableMap(map);
    }
}
