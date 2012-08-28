package com.pi.common.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.common.database.world.SectorLocation;
import com.pi.common.world.SectorManager;
import com.pi.common.world.SectorManager.SectorStorage;

/**
 * Monitors the state of loaded sectors.
 * 
 * @author Westin
 * @see com.pi.common.world.SectorManager
 */
public class SectorMonitorPanel extends JPanel {

	/**
	 * Creates a sector manager panel with the specified Sector Manager
	 * providing the contents.
	 * 
	 * @see com.pi.common.world.SectorManager
	 * @param sm the information provider
	 */
	public SectorMonitorPanel(final SectorManager sm) {
		setLocation(0, 0);
		setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		setLayout(null);
		JTable tbl = new JTable(new SectorTableModel(sm));
		tbl.setLocation(0, 0);
		tbl.setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		tbl.setVisible(true);
		tbl.setFillsViewportHeight(true);
		add(tbl);
		setVisible(true);
	}

	/**
	 * Represents a table model based on information provided by the
	 * SectorManager.
	 * 
	 * @author Westin
	 * 
	 */
	private static final class SectorTableModel extends
			AbstractTableModel {
		/**
		 * The name of each column.
		 */
		private static final String[] COLUMN_NAMES = { "X",
				"Plane", "Z", "Last used", "Empty Sector",
				"Revision" };
		/**
		 * The class of each column.
		 */
		private static final Class<?>[] COLUMN_CLASSES = {
				String.class, String.class, String.class,
				String.class, String.class, String.class };

		/**
		 * The information provider.
		 */
		private final SectorManager sm;

		/**
		 * Creates a Sector table model with the specified SectorManager as the
		 * information provider.
		 * 
		 * @param svr the information provider
		 */
		private SectorTableModel(final SectorManager svr) {
			this.sm = svr;
		}

		@Override
		public int getRowCount() {
			return sm.loadedMap().size();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			SectorLocation[] keyArr =
					sm.loadedMap()
							.keySet()
							.toArray(
									new SectorLocation[sm
											.loadedMap()
											.keySet().size()]);
			if (row < keyArr.length) {
				SectorLocation key = keyArr[row];
				SectorStorage ss = sm.loadedMap().get(key);
				if (ss != null) {
					switch (col) {
					case 0:
						return key.getSectorX() + "";
					case 1:
						return key.getPlane() + "";
					case 2:
						return key.getSectorZ() + "";
					case 3:
						return ""
								+ (System.currentTimeMillis() - ss
										.getLastUsedTime());
					case 4:
						return Boolean.toString(ss.isEmpty());
					case 5:
						if (ss.getSectorRaw() != null) {
							return ss.getSectorRaw()
									.getRevision() + "";
						} else {
							return "-1";
						}
					default:
						return "";
					}
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