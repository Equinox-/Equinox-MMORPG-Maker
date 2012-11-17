package com.pi.client.debug;

import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.client.Client;
import com.pi.client.entity.ClientEntity;
import com.pi.common.debug.PIResourceViewer;
import com.pi.common.game.entity.Entity;

/**
 * Monitors entities using a graphical JPanel.
 * 
 * @author Westin
 * @see com.pi.client.entity.ClientEntityManager
 * 
 */
public class EntityMonitorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	/**
	 * Creates an entity model panel using the provided client as the entity
	 * source.
	 * 
	 * @param client the client
	 * @see com.pi.client.entity.ClientEntityManager
	 */
	public EntityMonitorPanel(final Client client) {
		setLocation(0, 0);
		setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		setLayout(null);
		JTable tbl = new JTable(new EntityTableModel(client));
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
		private static final long serialVersionUID = 1L;
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
				String.class, String.class, String.class,
				String.class };
		/**
		 * The information provider for the table.
		 */
		private final Client svr;

		/**
		 * Create a table with the provided entity manager as the information
		 * provider.
		 * 
		 * @param client the client instance
		 */
		private EntityTableModel(final Client client) {
			this.svr = client;
		}

		@Override
		public int getRowCount() {
			return svr.getEntityManager().entityCount() + 1;
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(final int sRow, final int col) {
			int row = sRow;
			if (row == 0) {
				return COLUMN_NAMES[col];
			}
			row--;
			Iterator<ClientEntity> itr =
					svr.getEntityManager().getEntities();
			ClientEntity sEnt = null;
			while (row >= 0) {
				row--;
				sEnt = itr.next();
			}
			if (sEnt != null) {
				Entity ent = sEnt.getWrappedEntity();
				//EntityDef def =
				//		svr.getDefs().getEntityLoader()
				//				.getDef(ent.getEntityDef());
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
					/*if (def != null) {
						return Boolean.toString(def
								.getLogicCLass().length() > 0);
					} else {
						return "No Def";
					}*/
					return ""; // TODO
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
