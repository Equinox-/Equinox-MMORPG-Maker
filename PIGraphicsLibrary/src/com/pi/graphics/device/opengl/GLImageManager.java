package com.pi.graphics.device.opengl;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.pi.common.database.io.GraphicsDirectories;
import com.pi.common.game.ObjectHeap;
import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.GraphicsStorage;
import com.pi.graphics.device.ImageManager;

/**
 * Class for loading images into the ram using a threaded model.
 * 
 * @author Westin
 * 
 */
public class GLImageManager extends ImageManager<Texture> {
	/**
	 * Create an image manager linked to the specified object.
	 * 
	 * @param device the device to link to
	 */
	public GLImageManager(final DeviceRegistration device) {
		super(device);
	}

	@Override
	public final Texture fetchImage(final int graphic) {
		synchronized (getDataMap()) {
			if (!isRunning()) {
				return null;
			}
			ObjectHeap<GraphicsStorage> heap =
					getDataMap()
							.get(graphic >>> GraphicsDirectories.DIRECTORY_BIT_SHIFT);
			if (heap == null) {
				heap = new ObjectHeap<GraphicsStorage>();
				getDataMap()
						.set(graphic >>> GraphicsDirectories.DIRECTORY_BIT_SHIFT,
								heap);
			}
			GraphicsStorage tS =
					heap.get(graphic
							& GraphicsDirectories.FILE_MASK);
			if (tS == null) {
				addToLoadQueue(graphic);
				getDataMap().notify();
				return null;
			}
			tS.updateLastUsed();
			heap.set(graphic & GraphicsDirectories.FILE_MASK, tS);
			if (tS.getGraphic() != null) {
				if (tS.getGraphic() instanceof TextureData) {
					try {
						Texture tex =
								TextureIO
										.newTexture((TextureData) tS
												.getGraphic());
						tS.setGraphic(tex);
						return tex;
					} catch (Exception e) {
						getDeviceRegistration().getLog()
								.printStackTrace(e);
					}
				}
				if (tS.getGraphic() instanceof Texture) {
					return (Texture) tS.getGraphic();
				}
			}
			return null;
		}
	}

	@Override
	protected final TextureData loadImage(final File tex) {
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
				return null;
			}
		}
	}

	@Override
	protected final void dispose(final GraphicsStorage obj) {
		if (obj.getGraphic() != null) {
			((BufferedImage) obj.getGraphic()).flush();
		}
	}
}