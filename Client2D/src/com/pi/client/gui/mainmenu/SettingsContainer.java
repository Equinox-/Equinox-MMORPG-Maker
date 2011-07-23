package com.pi.client.gui.mainmenu;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.pi.client.graphics.device.DisplayManager.GraphicsMode;
import com.pi.client.gui.*;
import com.pi.client.gui.PIStyle.StyleType;

public class SettingsContainer extends PIContainer {
    private final MainMenu menu;
    private final PICheckbox opengl = new PICheckbox(), awt = new PICheckbox();

    public SettingsContainer(final MainMenu menu) {
	setStyle(StyleType.Normal, GUIKit.containerNormal);
	this.menu = menu;
	add(opengl);
	opengl.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (!menu.client.getDisplayManager().getMode()
			.equals(GraphicsMode.OpenGL)) {
		    menu.unregisterFromApplet();
		    menu.client.getDisplayManager()
			    .setMode(GraphicsMode.OpenGL);
		    menu.registerToApplet();
		}
	    }
	});
	add(awt);
	awt.addMouseListener(new MouseAdapter() {
	    @Override
	    public void mouseClicked(MouseEvent e) {
		if (!menu.client.getDisplayManager().getMode()
			.equals(GraphicsMode.AWT)) {
		    menu.unregisterFromApplet();
		    menu.client.getDisplayManager().setMode(
			    GraphicsMode.AWT);
		    menu.registerToApplet();
		}
	    }
	});
	opengl.setContent("OpenGL");
	awt.setContent("Safe Mode");
    }

    @Override
    public void update() {
	opengl.setVisible(menu.client.getDisplayManager().hasOpenGL());
	if (menu.client.getDisplayManager().getMode().equals(GraphicsMode.AWT)) {
	    awt.setChecked(true);
	    opengl.setChecked(false);
	} else {
	    opengl.setChecked(true);
	    awt.setChecked(false);
	}
    }

    @Override
    public void setSize(int width, int height) {
	super.setSize(width, height);
	awt.setLocation(width / 50, height / 50);
	awt.setSize(width / 5, height / 15);
	opengl.setLocation(width / 2, height / 50);
	opengl.setSize(width / 5, height / 15);
    }
}
