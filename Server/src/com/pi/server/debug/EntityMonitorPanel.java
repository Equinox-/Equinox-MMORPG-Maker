package com.pi.server.debug;

import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.common.debug.PIResourceViewer;
import com.pi.common.game.entity.Entity;
import com.pi.common.game.entity.comp.EntityComponent;
import com.pi.server.Server;
import com.pi.server.entity.ServerEntity;

/**
 * Monitors entities using a graphical JPanel.
 * 
 * @author Westin
 * @see com.pi.server.entity.ServerEntityManager
 * 
 */
public class EntityMonitorPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates an entity model panel using the provided server as the entity
	 * source.
	 * 
	 * @param server
	 *            the server
	 * @see com.pi.server.entity.ServerEntityManager
	 */
	public EntityMonitorPanel(final Server server) {
		setLocation(0, 0);
		setSize(PIResourceViewer.DEFAULT_WIDTH, PIResourceViewer.DEFAULT_HEIGHT);
		setLayout(null);
		JTable tbl = new JTable(new EntityTableModel(server));
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
	private static final class EntityTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		/**
		 * The names of the columns.
		 */
		private static final String[] COLUMN_NAMES = { "ID", "X", "Plane", "Z",
				"Layer", "Dir", "Components" };
		/**
		 * The classes of the column.
		 */
		private static final Class<?>[] COLUMN_CLASSES = { String.class,
				String.class, String.class, String.class, String.class,
				String.class, String.class };
		/**
		 * The information provider for the table.
		 */
		private final Server svr;

		/**
		 * Create a table with the provided entity manager as the information
		 * provider.
		 * 
		 * @param server
		 *            the server instance
		 */
		private EntityTableModel(final Server server) {
			this.svr = server;
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
			Iterator<ServerEntity> itr = svr.getEntityManager().getEntities();
			ServerEntity sEnt = null;
			while (row >= 0) {
				row--;
				sEnt = itr.next();
			}
			if (sEnt != null) {
				Entity ent = sEnt.getWrappedEntity();
				// EntityDef def = svr.getDefs().getEntityLoader()
				// .getDef(ent.getEntityDef());
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
					// return ent.getEntityDef();
				case 7:
					StringBuilder b = new StringBuilder();
					for (EntityComponent c : ent.getComponents()) {
						if (c != null) {
							b.append(c.getClass().getSimpleName()
									.replace("Entity", "")
									.replace("Component", ""));
						}
					}
					return b.toString();
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
		public boolean isCellEditable(final int row, final int col) {
			return false;
		}
	}
}
