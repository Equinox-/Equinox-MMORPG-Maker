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
	 * The IP this client will connect to.
	 */
	private final String ip;
	/**
	 * The port this client will connect to.
	 */
	private final int port;

	/**
	 * Create a client applet with the default width and height.
	 * 
	 * @param sIp the IP address to connect to
	 * @param sPort the port to connect to
	 * 
	 * @see com.pi.client.constants.Constants#DEFAULT_CLIENT_WIDTH
	 * @see com.pi.client.constants.Constants#DEFAULT_CLIENT_HEIGHT
	 */
	public ClientApplet(final String sIp, final int sPort) {
		setVisible(true);
		setSize(Constants.DEFAULT_CLIENT_WIDTH,
				Constants.DEFAULT_CLIENT_HEIGHT);
		setLayout(null);
		setFocusTraversalKeysEnabled(false);
		this.ip = sIp;
		this.port = sPort;
	}

	@Override
	public final void init() {
		super.init();
		this.client = new Client(this, ip, port);
	}

	@Override
	public final void destroy() {
		super.destroy();
		this.client.dispose();
	}
}
