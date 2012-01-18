package com.pi.client.clientviewer;

import java.applet.Applet;

import com.pi.client.Client;

public class ClientApplet extends Applet {
    private static final long serialVersionUID = 1L;
    public Client client;

    public ClientApplet() {
	setVisible(true);
	setSize(500, 500);
	setLayout(null);
	setFocusTraversalKeysEnabled(false);
    }

    @Override
    public void init() {
	super.init();
	this.client = new Client(this);
    }

    @Override
    public void destroy() {
	super.destroy();
	this.client.dispose();
    }
}
