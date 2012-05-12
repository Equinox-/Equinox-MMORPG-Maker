package com.pi.gui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import com.pi.graphics.device.IGraphics;

public class PIPopup implements MouseListener {
    private String content;
    private boolean loadingBar;
    private boolean closeable;
    private boolean isVisible = false;
    private Rectangle closeButton = null;

    public void setContent(String s) {
	this.content = s;
    }

    public boolean isVisible() {
	return isVisible;
    }

    public void displayLoadingBar(boolean value) {
	this.loadingBar = value;
    }

    public void setVisible(boolean value) {
	this.isVisible = value;
    }

    public void setCloseable(boolean value) {
	this.closeable = value;
    }

    public void render(IGraphics g, int width, int height) {
	if (isVisible) {
	    Rectangle container = new Rectangle(width / 4, height / 4,
		    width / 2, height / 2);
	    if (content == null || content.length() <= 0)
		container.setBounds(width / 4,
			(height / 2) - (height / 15 + 4), width / 2,
			height / 15 + 4);
	    if (GUIKit.containerNormal.background != null) {
		g.setColor(GUIKit.containerNormal.background);
		g.fillRect(container);
	    }
	    if (GUIKit.containerNormal.border != null) {
		g.setColor(GUIKit.containerNormal.border);
		g.drawRect(container);
	    }
	    if (loadingBar) {
		g.drawImage(GUIKit.Graphics.loader, container.x + 2,
			container.y + container.height - 2 - (height / 15),
			container.width - 4, height / 15);
	    }
	    if (content != null) {
		g.drawWrappedText(new Rectangle(container.x + 2, container.y
			+ 2 + (closeable ? 20 : 0), container.width - 4,
			container.height - (loadingBar ? (height / 15) : 0) - 4
				- (closeable ? 20 : 0)),
			GUIKit.containerNormal.font, content,
			GUIKit.containerNormal.foreground,
			GUIKit.containerNormal.hAlign,
			GUIKit.containerNormal.vAlign);
	    }
	    if (closeable) {
		g.drawText("X", container.x + container.width - 18,
			container.y + 2, GUIKit.containerNormal.font,
			GUIKit.containerNormal.foreground);
		closeButton = new Rectangle(container.x + container.width - 18,
			container.y + 2, 16, 16);
	    }
	}
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	if (isVisible && closeable && closeButton != null) {
	    if (closeButton.contains(e.getPoint())) {
		isVisible = false;
	    }
	}
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }
}
