package com.pi.server.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.common.contants.GlobalConstants;
import com.pi.common.net.client.NetClient;
import com.pi.server.client.Client;
import com.pi.server.client.ClientManager;

public class ClientMonitorPanel extends JPanel {
    private static final long serialVersionUID = GlobalConstants.serialVersionUID;
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
	private static final long serialVersionUID = GlobalConstants.serialVersionUID;
	String[] colName = { "ID", "IP", "Account", "Upload", "Download" };
	Class<?>[] colClass = { String.class, String.class, String.class,
		String.class, String.class };

	@Override
	public int getRowCount() {
	    return svr.registeredClients().size();
	}

	@Override
	public int getColumnCount() {
	    return colName.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
	    Integer[] keyArr = svr
		    .registeredClients()
		    .keySet()
		    .toArray(
			    new Integer[svr.registeredClients().keySet().size()]);
	    if (row < keyArr.length) {
		Integer key = keyArr[row];
		Client cli = svr.getClient(key);
		if (cli == null || cli.getNetClient() == null)
		    return "";
		NetClient c = cli.getNetClient();
		switch (col) {
		case 0:
		    return c.getID() + "";
		case 1:
		    return c.getSocket() != null
			    && c.getSocket().getInetAddress() != null ? c
			    .getSocket().getInetAddress().getHostAddress() : "";
		case 2:
		    return "";
		case 3:
		    return c.getUploadSpeed() + "b/s";
		case 4:
		    return c.getDownloadSpeed() + "b/s";
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
