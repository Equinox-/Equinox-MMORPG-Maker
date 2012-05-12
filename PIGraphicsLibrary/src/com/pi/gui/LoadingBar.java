package com.pi.gui;

import java.awt.Rectangle;

import com.pi.graphics.device.DeviceRegistration;
import com.pi.graphics.device.IGraphics;
import com.pi.graphics.device.Renderable;

public class LoadingBar implements Renderable {
    private final DeviceRegistration client;

    public LoadingBar(final DeviceRegistration client) {
	this.client = client;
    }

    @Override
    public void render(IGraphics g) {
	int width = client.getContainer().getWidth();
	int height = client.getContainer().getHeight();
	Rectangle r = new Rectangle((width / 2) - 125, (height / 2) - 48, 250,
		76);
	if (GUIKit.loadingBar != null) {
	    if (GUIKit.loadingBar.background != null) {
		g.setColor(GUIKit.loadingBar.background);
		g.fillRect(r);
	    }
	    if (GUIKit.loadingBar.bgImage != null) {
		g.drawTiledImage(GUIKit.loadingBar.bgImage, r,
			GUIKit.loadingBar.stretchBackgroundX,
			GUIKit.loadingBar.stretchBackgroundY);
	    }
	    if (GUIKit.loadingBar.border != null) {
		g.setColor(GUIKit.loadingBar.border);
		g.drawRect(r);
	    }
	    if (GUIKit.Graphics.loader != null)
		g.drawImage(GUIKit.Graphics.loader, r.x + 2, r.y + 2,
			r.width - 4, r.height / 2);
	    r.setBounds(r.x + 2, r.y + r.height / 2 + 4, r.width - 4,
		    (r.height / 2) - 6);
	    g.drawWrappedText(r, GUIKit.loadingBar.font, client.getLog()
		    .getLastMessage(), GUIKit.loadingBar.foreground,
		    GUIKit.loadingBar.hAlign, GUIKit.loadingBar.vAlign);
	}
    }
}
