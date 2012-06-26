package com.pi.gui;

import java.awt.Rectangle;

import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.Renderable;

/**
 * A loading bar that shows a status and a loading animation.
 * 
 * @author Westin
 * 
 */
public class LoadingBar implements Renderable {
	/**
	 * The width of the loading bar.
	 */
	private static final int LOADING_BAR_WIDTH = 250;

	/**
	 * The height of the loading bar.
	 */
	private static final int LOADING_BAR_HEIGHT = 76;

	/**
	 * The device this loading bar is registered to.
	 */
	private final DeviceRegistration device;

	/**
	 * Create a new loading bar bound to a specific device.
	 * 
	 * @param sDevice the device to bind to
	 */
	public LoadingBar(final DeviceRegistration sDevice) {
		this.device = sDevice;
	}

	@Override
	public final void render(final IGraphics g) {
		int width = device.getContainer().getWidth();
		int height = device.getContainer().getHeight();
		Rectangle r =
				new Rectangle((width / 2)
						- (LOADING_BAR_WIDTH / 2), (height / 2)
						- (LOADING_BAR_HEIGHT / 2),
						LOADING_BAR_WIDTH, LOADING_BAR_HEIGHT);
		if (GUIKit.DEFAULT_LOADING_BAR_STYLE != null) {
			if (GUIKit.DEFAULT_LOADING_BAR_STYLE.background != null) {
				g.setColor(GUIKit.DEFAULT_LOADING_BAR_STYLE.background);
				g.fillRect(r);
			}
			if (GUIKit.DEFAULT_LOADING_BAR_STYLE.bgImage != null) {
				g.drawTiledImage(
						GUIKit.DEFAULT_LOADING_BAR_STYLE.bgImage,
						r,
						GUIKit.DEFAULT_LOADING_BAR_STYLE.stretchBackgroundX,
						GUIKit.DEFAULT_LOADING_BAR_STYLE.stretchBackgroundY);
			}
			if (GUIKit.DEFAULT_LOADING_BAR_STYLE.border != null) {
				g.setColor(GUIKit.DEFAULT_LOADING_BAR_STYLE.border);
				g.drawRect(r);
			}
			if (GUIKit.Graphics.LOADER != null) {
				g.drawImage(GUIKit.Graphics.LOADER, r.x + 2,
						r.y + 2, r.width - 4, r.height / 2);
			}
			r.setBounds(r.x + 2, r.y + r.height / 2 + 4,
					r.width - 4, (r.height / 2) - 6);
			g.drawWrappedText(r,
					GUIKit.DEFAULT_LOADING_BAR_STYLE.font,
					device.getLog().getLastMessage(),
					GUIKit.DEFAULT_LOADING_BAR_STYLE.foreground,
					GUIKit.DEFAULT_LOADING_BAR_STYLE.hAlign,
					GUIKit.DEFAULT_LOADING_BAR_STYLE.vAlign);
		}
	}
}
