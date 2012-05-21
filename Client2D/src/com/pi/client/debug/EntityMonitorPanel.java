package com.pi.client.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.client.entity.ClientEntityManager;
import com.pi.common.game.Entity;

public class EntityMonitorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final ClientEntityManager svr;
	private ThreadTableModel table_model = new ThreadTableModel();
	private JTable tbl;

	public EntityMonitorPanel(ClientEntityManager svr) {
		this.svr = svr;
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
		private static final long serialVersionUID = 1L;
		String[] colName = { "ID", "X", "Plane", "Z", "Layer", "Dir", "Def" };
		Class<?>[] colClass = { String.class, String.class, String.class,
				String.class, String.class, String.class };

		@Override
		public int getRowCount() {
			return svr.registeredEntities().size();
		}

		@Override
		public int getColumnCount() {
			return colName.length;
		}

		@Override
		public Object getValueAt(int row, int col) {
			Integer[] keyArr = svr
					.registeredEntities()
					.keySet()
					.toArray(
							new Integer[svr.registeredEntities().keySet()
									.size()]);
			if (row < keyArr.length) {
				Integer key = keyArr[row];
				Entity ent = svr.getEntity(key);
				if (ent == null)
					return "";
				switch (col) {
				case 0:
					return ent.getEntityID() + "";
				case 1:
					return ent.getGlobalX() + "";
				case 2:
					return ent.getPlane() + "";
				case 3:
					return ent.getGlobalZ() + "";
				case 4:
					return ent.getLayer() + "";
				case 5:
					return ent.getDir() + "";
				case 6:
					return ent.getEntityDef()
							+ "-"
							+ svr.getClient().getDefs().getEntityLoader()
									.getDef(ent.getEntityDef()) != null ? "Loaded"
							: (svr.getClient().getDefs().getEntityLoader()
									.isEmpty(ent.getEntityDef()) ? "Empty"
									: "Unloaded");
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
