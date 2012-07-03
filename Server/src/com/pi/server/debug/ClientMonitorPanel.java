package com.pi.server.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.server.client.Client;
import com.pi.server.client.ClientManager;
import com.pi.server.net.NetServerClient;

/**
 * Monitors the client manager using a graphical JPanel.
 * 
 * @see com.pi.server.client.ClientManager
 * @author Westin
 * 
 */
public class ClientMonitorPanel extends JPanel {

	/**
	 * Creates a client monitoring panel for the given client manager.
	 * 
	 * @param sSvr the client manager to bind to
	 */
	public ClientMonitorPanel(final ClientManager sSvr) {
		setLocation(0, 0);
		setSize(500, 500);
		setLayout(null);
		JTable tbl = new JTable(new ClientTableModel(sSvr));
		tbl.setLocation(0, 0);
		tbl.setSize(500, 500);
		tbl.setVisible(true);
		tbl.setFillsViewportHeight(true);
		add(tbl);
		setVisible(true);
	}

	/**
	 * A table model that uses a client manager to provide the information.
	 * 
	 * @author Westin
	 * 
	 */
	private static final class ClientTableModel extends
			AbstractTableModel {
		/**
		 * The column names.
		 */
		private static final String[] COLUMN_NAMES = { "ID",
				"IP", "Account", "Upload", "Download" };
		/**
		 * The column classes.
		 */
		private static final Class<?>[] COLUMN_CLASSES = {
				String.class, String.class, String.class,
				String.class, String.class };

		/**
		 * The client manager bound to this monitor.
		 */
		private final ClientManager svr;

		/**
		 * Creates a client monitoring table model for the given client manager.
		 * 
		 * @param sSvr the client manager to bind to
		 */
		private ClientTableModel(final ClientManager sSvr) {
			this.svr = sSvr;
		}

		@Override
		public int getRowCount() {
			return svr.registeredClients().numElements();
		}

		@Override
		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			int key = 0, i = 0;
			while (true) {
				if (key >= svr.registeredClients().capacity()) {
					key = -1;
					break;
				}
				if (svr.getClient(key) != null) {
					if (i == row) {
						break;
					}
					i++;
				}
				key++;
			}
			if (key != -1) {
				Client cli = svr.getClient(key);
				if (cli == null || cli.getNetClient() == null) {
					return "";
				}
				NetServerClient c = cli.getNetClient();
				switch (col) {
				case 0:
					return c.getID();
				case 1:
					if (c.isConnected()) {
						return c.getHostAddress();
					} else {
						return "NC";
					}
				case 2:
					if (cli.getAccount() != null) {
						return cli.getAccount().getUsername();
					} else {
						return "NB";
					}
				case 3:
					return (c.getUploadSpeed() * 8) + "b/s";
				case 4:
					return (c.getDownloadSpeed() * 8) + "b/s";
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
