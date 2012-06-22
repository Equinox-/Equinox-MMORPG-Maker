package com.pi.server.debug;

import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.common.database.def.EntityDef;
import com.pi.common.game.Entity;
import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

public class EntityMonitorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final Server svr;
	private ThreadTableModel table_model = new ThreadTableModel();
	private JTable tbl;

	public EntityMonitorPanel(Server svr) {
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
		String[] colName = { "ID", "X", "Plane", "Z", "Layer", "Dir", "Def",
				"Brain" };
		Class<?>[] colClass = { String.class, String.class, String.class,
				String.class, String.class, String.class, String.class };

		@Override
		public int getRowCount() {
			return svr.getServerEntityManager().entityCount() + 1;
		}

		@Override
		public int getColumnCount() {
			return colName.length;
		}

		@Override
		public Object getValueAt(int row, int col) {
			if (row == 0) {
				return colName[col];
			}
			row--;
			Iterator<ServerEntity> itr = svr.getServerEntityManager()
					.getEntities();
			ServerEntity sEnt = null;
			while (row >= 0) {
				row--;
				sEnt = itr.next();
			}
			if (sEnt != null) {
				Entity ent = sEnt.getWrappedEntity();
				EntityDef def = svr.getDefs().getEntityLoader()
						.getDef(ent.getEntityDef());
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
					return ent.getLayer();
				case 5:
					return ent.getDir() + "";
				case 6:
					return ent.getEntityDef();
				case 7:
					return def != null ? Boolean.toString(def.getLogicCLass()
							.length() > 0) : "No Def";
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
