package com.pi.client.graphics;

import com.pi.client.Client;
import com.pi.client.graphics.device.IGraphics;
import com.pi.client.gui.LoadingBar;
import com.pi.client.gui.mainmenu.MainMenu;

public class RenderLoop implements Renderable {
    private final Client client;
    private MainMenu menu;
    private final LoadingBar loadBar;

    public RenderLoop(Client client) {
	this.client = client;
	this.loadBar = new LoadingBar(client);
    }

    public void postInitiation() {
	this.menu = new MainMenu(client);
    }

    @Override
    public void render(IGraphics g) {
	if (this.menu != null) {
	    this.menu.render(g);
	} else {
	    loadBar.render(g);
	}
    }

    public MainMenu getMainMenu() {
	return menu;
    }
}
