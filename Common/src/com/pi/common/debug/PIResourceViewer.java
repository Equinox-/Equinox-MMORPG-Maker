package com.pi.common.debug;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class PIResourceViewer extends JFrame {
	private static final long serialVersionUID = 1L;
	public final JTabbedPane tabs;

	public PIResourceViewer(String type) {
		super("Resource Viewer - " + type);
		this.tabs = new JTabbedPane();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocation(0, 0);
		setSize(500, 500);
		tabs.setLocation(0, 0);
		tabs.setSize(500, 500);
		tabs.setVisible(true);
		getContentPane().add(tabs);
		setVisible(true);
		setLayout(null);
		new Thread() {
			@Override
			public void run() {
				while (isVisible()) {
					repaint();
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void addTab(String name, Component panel) {
		panel.setSize(tabs.getSize());
		panel.setLocation(0, 0);
		tabs.addTab(name, panel);
	}

	public void removeTab(int idx) {
		tabs.removeTabAt(idx);
	}

	public void removeTab(String name) {
		for (int i = 0; i < tabs.getTabCount(); i++) {
			if (tabs.getTitleAt(i).equalsIgnoreCase(name)) {
				removeTab(i);
				return;
			}
		}
	}

	public Component getTab(int idx) {
		return tabs.getTabComponentAt(idx);
	}

	public Component getTab(String name) {
		for (int i = 0; i < tabs.getTabCount(); i++) {
			if (tabs.getTitleAt(i).equalsIgnoreCase(name)) {
				return getTab(i);
			}
		}
		return null;
	}
}
