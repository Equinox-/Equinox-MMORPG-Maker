package com.pi.client.clientviewer;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class ClientViewerFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final ClientApplet cApplet;

    public ClientViewerFrame() {
	super("Client");
	setSize(500, 500);
	setLocation(0, 0);
	setVisible(true);
	cApplet = new ClientApplet();
	add(cApplet);
	cApplet.init();
	cApplet.start();
	addWindowListener(new WindowAdapter() {
	    @Override
	    public void windowClosing(WindowEvent e) {
		cApplet.destroy();
	    }
	});
	addComponentListener(new ComponentAdapter() {
	    @Override
	    public void componentResized(ComponentEvent e) {
		cApplet.setSize(getSize());
	    }
	});
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void destroyFrame() {
	super.dispose();
    }

    public static void main(String[] args) {
	new ClientViewerFrame();
    }
}
