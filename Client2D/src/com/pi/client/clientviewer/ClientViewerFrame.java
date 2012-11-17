package com.pi.client.clientviewer;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.pi.client.constants.Constants;

/**
 * A frame based standalone container for a client.
 * 
 * @author Westin
 * 
 */
public class ClientViewerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	/**
	 * The client applet that the frame displays.
	 */
	private final ClientApplet cApplet;

	/**
	 * Creates a client viewer frame instance with the default width and height,
	 * as defined in the {@link com.pi.client.constants.Constants} class.
	 * 
	 * @param sIp the IP address to connect to
	 * @param sPort the port to connect to
	 * 
	 * @see com.pi.client.constants.Constants#DEFAULT_CLIENT_WIDTH
	 * @see com.pi.client.constants.Constants#DEFAULT_CLIENT_HEIGHT
	 */
	public ClientViewerFrame(final String sIp, final int sPort) {
		super("Client");
		setSize(Constants.DEFAULT_CLIENT_WIDTH,
				Constants.DEFAULT_CLIENT_HEIGHT);
		setLocation(0, 0);
		setVisible(true);
		cApplet = new ClientApplet(sIp, sPort);
		getContentPane().add(cApplet);
		cApplet.init();
		cApplet.start();
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				cApplet.destroy();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				cApplet.setSize(getContentPane().getSize());
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * Provides the default launch method for the ClientViewerFrame.
	 * 
	 * @param args unused.
	 */
	public static void main(final String[] args) {
		String ip = Constants.NETWORK_IP;
		int port = Constants.NETWORK_PORT;
		if (args.length == 1) {
			String[] parts = ip.split(":");
			if (parts.length == 2) {
				ip = parts[0];
				port = Integer.valueOf(parts[1]);
			} else {
				ip = parts[0];
			}
		}
		new ClientViewerFrame(ip, port);
	}
}
