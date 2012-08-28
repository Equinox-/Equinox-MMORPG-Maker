package com.pi.graphics.device.awt;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.GraphicsStorage;
import com.pi.graphics.device.ImageManager;

/**
 * Class for loading images into the ram using a threaded model.
 * 
 * @author Westin
 * 
 */
public class AwtImageManager extends ImageManager<BufferedImage> {
	/**
	 * Create an image manager linked to the specified object.
	 * 
	 * @param device the device to link to
	 */
	public AwtImageManager(final DeviceRegistration device) {
		super(device);
	}

	@Override
	protected BufferedImage loadImage(File f) {
		if (f == null) {
			return null;
		} else {
			try {
				return ImageIO.read(f);
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	protected void dispose(GraphicsStorage obj) {
		if (obj.getGraphic() != null) {
			((BufferedImage) obj.getGraphic()).flush();
		}
	}
}
