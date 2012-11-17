package com.pi.common.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 * A panel for displaying the threads in a thread group.
 * 
 * @author Westin
 * 
 */
public class ThreadMonitorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	/**
	 * Creates a thread monitor panel bound to the specified thread group.
	 * 
	 * @param tg the thread group to bind to
	 */
	public ThreadMonitorPanel(final ThreadGroup tg) {
		setLayout(null);
		setLocation(0, 0);
		setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		JTable tbl = new JTable(new ThreadTableModel(tg));
		tbl.setLocation(0, 0);
		tbl.setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		tbl.setVisible(true);
		tbl.setFillsViewportHeight(true);
		add(tbl);
		setVisible(true);
	}

	/**
	 * A table model that displays the threads running in a thread group.
	 * 
	 * @author Westin
	 * 
	 */
	private static final class ThreadTableModel extends
			AbstractTableModel {
		private static final long serialVersionUID = 1L;
		/**
		 * The thread group this table model is bound to.
		 */
		private final ThreadGroup tg;

		/**
		 * Create a thread table model with the specified thread group bound to
		 * it.
		 * 
		 * @param tG the thread group to bind
		 */
		private ThreadTableModel(final ThreadGroup tG) {
			this.tg = tG;
		}

		/**
		 * The column names.
		 */
		private static final String[] COLUMN_NAMES = { "ID",
				"Name", "Priority", "State" };
		/**
		 * The class type for each column.
		 */
		private static final Class<?>[] COLUMN_CLASSES = {
				Integer.class, String.class, Integer.class,
				String.class };

		@Override
		public int getRowCount() {
			return tg.activeCount();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			Thread[] thrds = new Thread[tg.activeCount()];
			tg.enumerate(thrds);
			if (row < thrds.length) {
				Thread t = thrds[row];
				switch (col) {
				case 0:
					return t.getId();
				case 1:
					return t.getName();
				case 2:
					return t.getPriority();
				case 3:
					if (t.getState() != null) {
						return t.getState().toString();
					} else {
						return "null";
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
