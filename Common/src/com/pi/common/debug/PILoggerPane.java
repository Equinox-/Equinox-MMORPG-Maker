package com.pi.common.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class PILoggerPane extends JScrollPane {
	private static final long serialVersionUID = 1L;
	public PrintStream logOut;

	public PILoggerPane() {
		super();
		final JTextPane tPane = new JTextPane();
		setSize(500, 450);
		setAutoscrolls(true);
		tPane.setAutoscrolls(true);
		setLocation(0, 0);
		tPane.setSize(500, 450);
		tPane.setLocation(0, 0);
		setSize(500, 450);
		setLocation(0, 0);
		add(tPane);
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
