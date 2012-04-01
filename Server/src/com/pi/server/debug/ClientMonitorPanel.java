package com.pi.server.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.server.client.Client;
import com.pi.server.client.ClientManager;
import com.pi.server.net.NetServerClient;

public class ClientMonitorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ClientManager svr;
    private ClientTableModel table_model = new ClientTableModel();
    private JTable tbl;

    public ClientMonitorPanel(ClientManager svr) {
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

    private class ClientTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	String[] colName = { "ID", "IP", "Account", "Upload", "Download" };
	Class<?>[] colClass = { String.class, String.class, String.class,
		String.class, String.class };

	@Override
	public int getRowCount() {
	    return svr.registeredClients().numElements();
	}

	@Override
	public int getColumnCount() {
	    return colName.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
	    int key = 0, i = 0;
	    while (true) {
		if (key >= svr.registeredClients().capacity()) {
		    key = -1;
		    break;
		}
		if (svr.getClient(key) != null) {
		    if (i == row)
			break;
		    i++;
		}
		key++;
	    }
	    if (key != -1) {
		Client cli = svr.getClient(key);
		if (cli == null || cli.getNetClient() == null)
		    return "";
		NetServerClient c = cli.getNetClient();
		switch (col) {
		case 0:
		    return c.getID();
		case 1:
		    return c.isConnected() ? c.getHostAddress() : "NC";
		case 2:
		    return cli.getAccount() != null ? cli.getAccount()
			    .getUsername() : "";
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
