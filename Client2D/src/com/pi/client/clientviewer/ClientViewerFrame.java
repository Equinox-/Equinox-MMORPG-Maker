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
	/**
	 * The client applet that the frame displays.
	 */
	private final ClientApplet cApplet;

	/**
	 * Creates a client viewer frame instance with the default width and height,
	 * as defined in the {@link com.pi.client.constants.Constants} class.
	 * 
	 * @see com.pi.client.constants.Constants#DEFAULT_CLIENT_WIDTH
	 * @see com.pi.client.constants.Constants#DEFAULT_CLIENT_HEIGHT
	 */
	public ClientViewerFrame() {
		super("Client");
		setSize(Constants.DEFAULT_CLIENT_WIDTH,
				Constants.DEFAULT_CLIENT_HEIGHT);
		setLocation(0, 0);
		setVisible(true);
		cApplet = new ClientApplet();
		add(cApplet);
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
				cApplet.setSize(getSize());
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
		new ClientViewerFrame();
	}
}
