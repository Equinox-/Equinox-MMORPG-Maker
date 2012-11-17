package com.pi.common.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.pi.launcher.ServerConfiguration;

/**
 * A class providing a graphical frontend for the logger class.
 * 
 * @see PILogger
 * @author Westin
 * 
 */
public class PILoggerPane extends JPanel {
	private static final long serialVersionUID = 1L;
	/**
	 * A PrintStream for a logger to output to.
	 */
	private PrintStream logOut;
	/**
	 * The text pane that the print stream pushes text to.
	 */
	private JTextPane tPane;
	/**
	 * The scroll pane that text pane is contained in.
	 */
	private JScrollPane scrlPane;

	/**
	 * Create a scrolling text pane for displaying text.
	 */
	public PILoggerPane() {
		super();
		setLayout(null);
		tPane = new JTextPane();
		setSize(ServerConfiguration.DEFAULT_WIDTH,
				ServerConfiguration.DEFAULT_HEIGHT);
		setLocation(0, 0);
		tPane.setSize(ServerConfiguration.DEFAULT_WIDTH,
				ServerConfiguration.DEFAULT_HEIGHT);
		tPane.setLocation(0, 0);
		tPane.setAutoscrolls(true);
		scrlPane = new JScrollPane(tPane);
		scrlPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrlPane.setAutoscrolls(true);
		scrlPane.setSize(ServerConfiguration.DEFAULT_WIDTH,
				ServerConfiguration.DEFAULT_HEIGHT);
		scrlPane.setLocation(0, 0);
		add(scrlPane);
		logOut = new PrintStream(new OutputStream() {
			@Override
			public void write(final int arg0) throws IOException {
				tPane.setText(tPane.getText() + ((char) arg0));
			}

			@Override
			public void write(final byte[] byts, final int off,
					final int len) {
				tPane.setText(tPane.getText()
						+ new String(byts, off, len));
			}
		});
	}

	/**
	 * Gets the print stream that outputs to the text pane.
	 * 
	 * @return the print stream
	 */
	public final PrintStream getLogOutput() {
		return logOut;
	}
}
