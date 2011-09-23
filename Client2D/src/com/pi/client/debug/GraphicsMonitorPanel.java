package com.pi.client.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.client.graphics.device.GraphicsStorage;
import com.pi.client.graphics.device.IGraphics;
import com.pi.common.contants.GlobalConstants;

public class GraphicsMonitorPanel extends JPanel {
	private static final long serialVersionUID = GlobalConstants.serialVersionUID;
	private final IGraphics ig;
	private ThreadTableModel table_model = new ThreadTableModel();
	private JTable tbl;

	public GraphicsMonitorPanel(IGraphics ig) {
		this.ig = ig;
		setLocation(0, 0);
		setSize(500, 500);
		setLayout(null);
		tbl = new JTable(table_model);
		tbl.setLocation(0, 0);
		tbl.setSize(500, 500);
		tbl.setVisible(true);
		tbl.setFillsViewportHeight(true);
		add(tbl);
		setVisible(true);
	}

	private class ThreadTableModel extends AbstractTableModel {
		private static final long serialVersionUID = GlobalConstants.serialVersionUID;
		String[] colName = { "Name", "Last used", "Object" };
		Class<?>[] colClass = { String.class, String.class, String.class };

		@Override
		public int getRowCount() {
			return ig.loadedGraphics().size();
		}

		@Override
		public int getColumnCount() {
			return colName.length;
		}

		@Override
		public Object getValueAt(int row, int col) {
			String[] keyArr = ig.loadedGraphics().keySet()
					.toArray(new String[ig.loadedGraphics().keySet().size()]);
			if (row < keyArr.length) {
				String key = keyArr[row];
				GraphicsStorage ss = ig.loadedGraphics().get(key);
				switch (col) {
				case 0:
					return key;
				case 1:
					return ss != null ? "" + (System.currentTimeMillis()-ss.lastUsed) : -1 + "";
				case 2:
					return ss != null ? ss.getGraphic() != null ? ss
							.getGraphic().toString() : "null" : "null";
				default:
					return "";
				}
			}
			return "";
		}

		@Override
		public String getColumnName(int paramInt) {
			return colName[paramInt];
		}

		@Override
		public Class<?> getColumnClass(int paramInt) {
			return paramInt < colClass.length ? colClass[paramInt]
					: String.class;
		}

		@Override
		public boolean isCellEditable(int paramInt1, int paramInt2) {
			return false;
		}
	}
}
