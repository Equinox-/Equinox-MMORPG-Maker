package com.pi.client.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.pi.client.graphics.device.DisplayManager;
import com.pi.client.graphics.device.GraphicsStorage;

public class GraphicsMonitorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final DisplayManager ig;
    private ThreadTableModel table_model = new ThreadTableModel();
    private JTable tbl;

    public GraphicsMonitorPanel(DisplayManager displayManager) {
	this.ig = displayManager;
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
	    int idx = -1;
	    for (int i = 0; i < ig.loadedGraphics().capacity(); i++) {
		try {
		    if (ig.loadedGraphics().get(i) != null) {
			if (row-- == 0) {
			    idx = i;
			    break;
			}
		    }
		} catch (Exception e) {
		}
	    }
	    if (idx != -1) {
		try {
		    GraphicsStorage ss = ig.loadedGraphics().get(idx);
		    switch (col) {
		    case 0:
			return idx;
		    case 1:
			return ss != null ? ""
				+ (System.currentTimeMillis() - ss.lastUsed)
				: -1 + "";
		    case 2:
			return ss != null ? ss.getGraphic() != null ? ss
				.getGraphic().toString() : "null" : "null";
		    default:
			return "";
		    }
		} catch (Exception e) {
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
