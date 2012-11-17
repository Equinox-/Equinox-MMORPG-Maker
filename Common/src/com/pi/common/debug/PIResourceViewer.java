package com.pi.common.debug;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * A general resource viewer for monitoring various sub systems.
 * 
 * @author Westin
 * 
 */
public class PIResourceViewer extends JFrame {
	private static final long serialVersionUID = 1L;
	/**
	 * The default width of the resource viewer and sub components.
	 */
	public static final int DEFAULT_WIDTH = 500;
	/**
	 * The default height of the resource viewer and sub components.
	 */
	public static final int DEFAULT_HEIGHT = 500;
	/**
	 * The instance of a tabbed pane allowing for multiple monitors in a small
	 * space.
	 */
	private final JTabbedPane tabs;

	/**
	 * Constructs the resource viewer with the given title suffix.
	 * 
	 * @param type the suffix to the frame title.
	 */
	public PIResourceViewer(final String type) {
		super("Resource Viewer - " + type);
		this.tabs = new JTabbedPane();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocation(0, 0);
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		tabs.setLocation(0, 0);
		tabs.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		tabs.setVisible(true);
		getContentPane().add(tabs);
		setVisible(true);
		setLayout(null);
		Thread thread = new Thread() {
			@Override
			public void run() {
				while (isVisible()) {
					repaint();
					try {
						sleep(500L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.setName("ResourceViewerRepaint");
		thread.start();
	}

	/**
	 * Adds a tab with the given name and information panel to the tab strip.
	 * 
	 * @param name the tab name
	 * @param panel the tab contents
	 */
	public final void addTab(final String name,
			final Component panel) {
		panel.setSize(tabs.getSize());
		panel.setLocation(0, 0);
		tabs.addTab(name, panel);
	}

	/**
	 * Remove the tab at a given index from the tabbed pane.
	 * 
	 * @param idx the tab to remove
	 */
	public final void removeTab(final int idx) {
		tabs.removeTabAt(idx);
	}

	/**
	 * Remove the tab with the given name from the tabbed pane.
	 * 
	 * @param name the tab to remove
	 */
	public final void removeTab(final String name) {
		for (int i = 0; i < tabs.getTabCount(); i++) {
			if (tabs.getTitleAt(i).equalsIgnoreCase(name)) {
				removeTab(i);
				return;
			}
		}
	}

	/**
	 * Get the tab component at the specified index.
	 * 
	 * @param idx the tab index
	 * @return the tab component
	 */
	public final Component getTab(final int idx) {
		return tabs.getTabComponentAt(idx);
	}

	/**
	 * Get the tab component by the given tab name.
	 * 
	 * @param name the tab name
	 * @return the tab component
	 */
	public final Component getTab(final String name) {
		for (int i = 0; i < tabs.getTabCount(); i++) {
			if (tabs.getTitleAt(i).equalsIgnoreCase(name)) {
				return getTab(i);
			}
		}
		return null;
	}
}
