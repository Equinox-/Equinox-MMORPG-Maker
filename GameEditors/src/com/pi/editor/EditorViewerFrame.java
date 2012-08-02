package com.pi.editor;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class EditorViewerFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private final EditorApplet cApplet;

	public EditorViewerFrame() {
		super("Editor");
		setSize(500, 500);
		setLocation(0, 0);
		setVisible(true);
		cApplet = new EditorApplet();
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
		new EditorViewerFrame();
	}
}
