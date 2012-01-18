package com.pi.common.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class PILoggerPane extends JPanel {
    private static final long serialVersionUID = 1L;
    public PrintStream logOut;
    private JTextPane tPane;
    private JScrollPane scrlPane;

    public PILoggerPane() {
	super();
	setLayout(null);
	tPane = new JTextPane();
	setSize(500, 450);
	setLocation(0, 0);
	tPane.setSize(450, 450);
	tPane.setLocation(0, 0);
	tPane.setAutoscrolls(true);
	setSize(500, 450);
	setLocation(0, 0);
	scrlPane = new JScrollPane(tPane);
	scrlPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	scrlPane.setAutoscrolls(true);
	scrlPane.setSize(500, 450);
	scrlPane.setLocation(0, 0);
	add(scrlPane);
	logOut = new PrintStream(new OutputStream() {

	    @Override
	    public void write(int arg0) throws IOException {
		tPane.setText(tPane.getText() + ((char) arg0));
	    }

	    @Override
	    public void write(byte[] byts, int off, int len) {
		tPane.setText(tPane.getText() + new String(byts, off, len));
	    }
	});
    }
}
