package com.pi.client.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.client.entity.ClientEntity;
import com.pi.client.entity.ClientEntityManager;
import com.pi.common.database.def.EntityDef;
import com.pi.common.debug.PIResourceViewer;
import com.pi.common.game.Entity;

/**
 * Monitors entities using a graphical JPanel.
 * 
 * @author Westin
 * @see com.pi.client.entity.ClientEntityManager
 * 
 */
public class EntityMonitorPanel extends JPanel {
	/**
	 * The table displaying the entity list.
	 */
	private JTable tbl;

	/**
	 * Creates an entity model panel using the provided ClientEntityManager as
	 * the entity source.
	 * 
	 * @param server the entity manager source
	 * @see com.pi.client.entity.ClientEntityManager
	 */
	public EntityMonitorPanel(final ClientEntityManager server) {
		setLocation(0, 0);
		setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		setLayout(null);
		tbl = new JTable(new EntityTableModel(server));
		tbl.setLocation(0, 0);
		tbl.setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		tbl.setVisible(true);
		tbl.setFillsViewportHeight(true);
		add(tbl);
		setVisible(true);
	}

	/**
	 * The table model representing the information displayed.
	 * 
	 * @author Westin
	 * 
	 */
	private static final class EntityTableModel extends
			AbstractTableModel {
		/**
		 * The names of the columns.
		 */
		private static final String[] COLUMN_NAMES = { "ID",
				"X", "Plane", "Z", "Layer", "Dir", "Def" };
		/**
		 * The classes of the column.
		 */
		private static final Class<?>[] COLUMN_CLASSES = {
				String.class, String.class, String.class,
				String.class, String.class, String.class };
		/**
		 * The information provider for the table.
		 */
		private final ClientEntityManager svr;

		/**
		 * Create a table with the provided entity manager as the information
		 * provider.
		 * 
		 * @param server the entity manager instance
		 */
		private EntityTableModel(final ClientEntityManager server) {
			this.svr = server;
		}

		@Override
		public int getRowCount() {
			return svr.registeredEntities().size();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			Integer[] keyArr =
					svr.registeredEntities()
							.keySet()
							.toArray(
									new Integer[svr
											.registeredEntities()
											.keySet().size()]);
			if (row < keyArr.length) {
				Integer key = keyArr[row];
				ClientEntity cEnt = svr.getEntity(key);
				Entity ent = cEnt.getWrappedEntity();
				if (ent == null) {
					return "";
				}
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
					EntityDef def =
							svr.getClient().getDefs()
									.getEntityLoader()
									.getDef(ent.getEntityDef());
					if (def != null) {
						return ent.getEntityDef() + "-Loaded";
					} else if (svr.getClient().getDefs()
							.getEntityLoader()
							.isEmpty(ent.getEntityDef())) {
						return ent.getEntityDef() + "-Empty";
					} else {
						return ent.getEntityDef() + "-Unloaded";
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
