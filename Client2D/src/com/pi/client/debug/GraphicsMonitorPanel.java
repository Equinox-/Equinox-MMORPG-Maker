package com.pi.client.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.common.debug.PIResourceViewer;
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
	public GraphicsMonitorPanel(
			final DisplayManager displayManager) {
		setLocation(0, 0);
		setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		setLayout(null);
		JTable tbl =
				new JTable(
						new GraphicsTableModel(displayManager));
		tbl.setLocation(0, 0);
		tbl.setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		tbl.setVisible(true);
		tbl.setFillsViewportHeight(true);
		add(tbl);
		setVisible(true);
	}

	/**
	 * Represents a table model based on information provided by a display
	 * manager.
	 * 
	 * @author Westin
	 * 
	 */
	private static class GraphicsTableModel extends
			AbstractTableModel {
		/**
		 * The name of each column.
		 */
		private static final String[] COLUMN_NAMES = { "Name",
				"Last used", "Object" };
		/**
		 * The class of each column.
		 */
		private static final Class<?>[] COLUMN_CLASSES = {
				String.class, String.class, String.class };

		/**
		 * The information provider.
		 */
		private final DisplayManager ig;

		/**
		 * Creates a graphics table model with the provided display manager as
		 * the information provider.
		 * 
		 * @param displayManager the information provider.
		 */
		public GraphicsTableModel(
				final DisplayManager displayManager) {
			this.ig = displayManager;
		}

		@Override
		public int getRowCount() {
			return ig.loadedGraphics().capacity();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			int offset = row;
			int idx = -1;
			for (int i = 0; i < ig.loadedGraphics().capacity(); i++) {
				if (ig.loadedGraphics().get(i) != null) {
					if (offset-- == 0) {
						idx = i;
						break;
					}
				}
			}
			if (idx != -1) {
				GraphicsStorage ss =
						ig.loadedGraphics().get(idx);
				switch (col) {
				case 0:
					return idx;
				case 1:
					if (ss == null) {
						return "null";
					} else {
						return (System.currentTimeMillis() - ss
								.getLastUsedTime()) + "ms";
					}
				case 2:
					if (ss == null || ss.getGraphic() == null) {
						return "null";
					} else {
						return ss.getGraphic().toString();
					}
				default:
					return "";
				}
			}
			return "";
		}

		@Override
		public String getColumnName(final int col) {
			return COLUMN_NAMES[col];
		}

		@Override
		public Class<?> getColumnClass(final int col) {
			return COLUMN_CLASSES[col];
		}

		@Override
		public boolean isCellEditable(final int row,
				final int col) {
			return false;
		}
	}
}
