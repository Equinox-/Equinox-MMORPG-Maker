package com.pi.client.gui.game;

import com.pi.client.Client;
import com.pi.client.graphics.GameRenderLoop;
import com.pi.client.graphics.Renderable;
import com.pi.client.graphics.device.IGraphics;

public class MainGame implements Renderable {
    private final Client client;
    private final GameRenderLoop gameRenderLoop;

    public MainGame(final Client client) {
	this.client = client;
	this.gameRenderLoop = new GameRenderLoop(client);
    }

    @Override
    public void render(IGraphics g) {
	this.gameRenderLoop.render(g);
    }
}
