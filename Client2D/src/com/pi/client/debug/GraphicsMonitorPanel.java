package com.pi.client.debug;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.pi.client.database.Paths;
import com.pi.common.database.io.GraphicsDirectories;
import com.pi.common.debug.PIResourceViewer;
import com.pi.common.game.ObjectHeap;
import com.pi.graphics.device.DisplayManager;
import com.pi.graphics.device.GraphicsStorage;

/**
 * Monitors the state of loaded graphics.
 * 
 * @author Westin
 * @see com.pi.graphics.device.DisplayManager
 */
public class GraphicsMonitorPanel extends JPanel {
	/**
	 * Creates a graphics monitor panel with the specified display manager
	 * providing the contents.
	 * 
	 * @param displayManager the information provider.
	 * @see com.pi.graphics.device.DisplayManager
	 */
	public GraphicsMonitorPanel(final DisplayManager ig) {
		setLocation(0, 0);
		setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		setLayout(null);
		final JTextArea text = new JTextArea();
		text.setLocation(0, 0);
		text.setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		text.setVisible(true);
		text.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				StringBuilder build = new StringBuilder();
				for (int i = 0; i < ig.loadedGraphics()
						.capacity(); i++) {
					build.append(i);
					if (i >= 0
							&& i < GraphicsDirectories.values().length) {
						build.append(":  ");
						build.append(GraphicsDirectories
								.values()[i].name());
					}
					build.append('\n');
					ObjectHeap<GraphicsStorage> heap =
							ig.loadedGraphics().get(i);
					if (heap != null) {
						for (int f = 0; f < heap.capacity(); f++) {
							if (heap.get(f) != null) {
								File file =
										Paths.getGraphicsFile(i,
												f);
								build.append("  ");
								build.append(f);
								build.append("  ");
								if (file != null) {
									build.append(file
											.getAbsolutePath()
											.replace(
													Paths.getGraphicsDirectory()
															.getAbsolutePath(),
													""));
								} else {
									build.append("null file");
								}
								build.append('\n');
							}
						}
					}
				}
				text.setText(build.toString());
			}
		});
		JScrollPane scrll = new JScrollPane(text);
		scrll.setLocation(0, 0);
		scrll.setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		scrll.setVisible(true);
		add(scrll);
		setVisible(true);
	}
}
