package com.pi.client.clientviewer;

import java.applet.Applet;

import com.pi.client.Client;
import com.pi.client.constants.Constants;

/**
 * An applet based container for the Client.
 * 
 * @author Westin
 * 
 */
public class ClientApplet extends Applet {
	/**
	 * The currently bound client instance.
	 */
	private Client client;

	/**
	 * Create a client applet with the default width and height.
	 * 
	 * @see com.pi.client.constants.Constants#DEFAULT_CLIENT_WIDTH
	 * @see com.pi.client.constants.Constants#DEFAULT_CLIENT_HEIGHT
	 */
	public ClientApplet() {
		setVisible(true);
		setSize(Constants.DEFAULT_CLIENT_WIDTH,
				Constants.DEFAULT_CLIENT_HEIGHT);
		setLayout(null);
		setFocusTraversalKeysEnabled(false);
	}

	@Override
	public final void init() {
		super.init();
		this.client = new Client(this);
	}

	@Override
	public final void destroy() {
		super.destroy();
		this.client.dispose();
	}
}
