package com.pi.common.debug;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class ThreadMonitorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final ThreadGroup tg;
    private ThreadTableModel table_model = new ThreadTableModel();
    private JTable tbl;

    public ThreadMonitorPanel(ThreadGroup tg) {
	this.tg = tg;
	setLayout(null);
	setLocation(0, 0);
	setSize(500, 500);
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
	String[] colName = { "ID", "Name", "Priority", "State" };
	Class<?>[] colClass = { Integer.class, String.class, Integer.class,
		String.class };

	@Override
	public int getRowCount() {
	    return tg.activeCount();
	}

	@Override
	public int getColumnCount() {
	    return colName.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
	    Thread[] thrds;
	    tg.enumerate(thrds = new Thread[tg.activeCount()]);
	    if (row < thrds.length) {
		Thread t = thrds[row];
		switch (col) {
		case 0:
		    return t.getId();
		case 1:
		    return t.getName();
		case 2:
		    return t.getPriority();
		case 3:
		    return t.getState() != null ? t.getState().toString()
			    : "null";
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
