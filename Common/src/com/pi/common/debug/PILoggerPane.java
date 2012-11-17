package com.pi.common.debug;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

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
		setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		setLocation(0, 0);
		tPane.setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		tPane.setLocation(0, 0);
		scrlPane = new JScrollPane(tPane);
		scrlPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrlPane.setSize(PIResourceViewer.DEFAULT_WIDTH,
				PIResourceViewer.DEFAULT_HEIGHT);
		scrlPane.setLocation(0, 0);
		add(scrlPane);
		logOut = new PrintStream(new OutputStream() {
			private StringBuilder tmpBuilder =
					new StringBuilder();

			@Override
			public void write(final int c) throws IOException {
				append(new String(new char[] { (char) c }));
			}

			@Override
			public void write(final byte[] byts, final int off,
					final int len) {
				append(new String(byts, off, len));
			}

			public void append(final String s) {
				String[] parts = s.split("\n", 2);
				if (parts.length == 1) {
					tmpBuilder.append(parts[0]);
				} else if (parts.length == 2) {
					tmpBuilder.append(parts[0]);
					tPane.setText(tmpBuilder.toString() + "\n"
							+ tPane.getText());
					tmpBuilder = new StringBuilder();
					append(parts[1]);
				}
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
