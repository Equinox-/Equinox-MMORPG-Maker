package com.pi.common.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * A class for logging to a print stream, and a file if wanted.
 * 
 * @author Westin
 * 
 */
public class PILogger {
	/**
	 * The default minimum displayed log level.
	 */
	private static final Level DEFAULT_LOGGING_LEVEL = Level.ALL;
	/**
	 * The last message displayed by this logger.
	 */
	private String lastMessage = "Loading...";
	/**
	 * The log entry handler.
	 */
	private final Handler handler;
	/**
	 * The log entry formatter.
	 */
	private final Formatter formatter;
	/**
	 * Parameters for the formatter.
	 */
	private boolean showDate = false, showTime = false,
			showCode = false, showLevel = true;
	/**
	 * The main output stream for this logger.
	 */
	private PrintStream streamOut;
	/**
	 * The secondary output stream for this logger. If this is null, then it
	 * won't be used.
	 */
	private PrintStream fileOut;

	/**
	 * Creates a logger outputting to the specified PrintStream and file, with
	 * the file being is optional.
	 * 
	 * @param f The file to output to, if this is <code>null</code> then it
	 *            won't be used, and no errors will be thrown.
	 * @param streamOutt The stream to output to.
	 */
	public PILogger(final PrintStream streamOutt, final File f) {
		if (f != null) {
			try {
				if (!f.exists()) {
					f.createNewFile();
				}
				if (f.exists()) {
					fileOut =
							new PrintStream(
									new FileOutputStream(f));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.streamOut = streamOutt;
		this.formatter = new Formatter() {
			private Date dat = new Date();
			private DateFormat dForm = DateFormat
					.getDateInstance();
			private DateFormat tForm = DateFormat
					.getTimeInstance();

			@Override
			public String format(final LogRecord record) {
				dat.setTime(record.getMillis());
				String source;
				if (record.getSourceClassName() != null) {
					source = record.getSourceClassName();
					if (record.getSourceMethodName() != null) {
						source +=
								":"
										+ record.getSourceMethodName();
					}
				} else {
					source = record.getLoggerName();
				}
				String message = formatMessage(record);
				String throwable = "";
				if (record.getThrown() != null) {
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					pw.println();
					record.getThrown().printStackTrace(pw);
					pw.close();
					throwable = sw.toString();
				}
				String s = "";
				String date = dForm.format(dat);
				String time = tForm.format(dat);
				if (showDate) {
					s += date + " ";
				}
				if (showTime) {
					s += time + " ";
				}
				if (showCode) {
					s += source + " ";
				}
				if (showLevel) {
					s +=
							"[" + record.getLevel().getName()
									+ "] ";
				}
				s += message;
				if (!throwable.equals("")) {
					s += "\n" + throwable;
				}
				return s;
			}
		};
		this.handler = new Handler() {
			@Override
			public void close() {
			}

			@Override
			public void flush() {
			}

			@Override
			public void publish(final LogRecord record) {
				String s = getFormatter().format(record);
				if (record.getLevel().intValue() >= getLevel()
						.intValue()) {
					lastMessage = record.getMessage();
					streamOut.println(s);
					if (fileOut != null) {
						fileOut.println(s);
					}
				}
			}
		};
		handler.setFormatter(this.formatter);
		handler.setLevel(DEFAULT_LOGGING_LEVEL);
	}

	/**
	 * Create a logger that only outputs to the specified print stream.
	 * 
	 * @see PILogger#PILogger(PrintStream, File)
	 * @param out The stream to output to.
	 */
	public PILogger(final PrintStream out) {
		this(out, null);
	}

	/**
	 * Publish a message to the logger, with a level of {@link Level#FINE}.
	 * 
	 * @param s the message to publish
	 */
	public final void fine(final String s) {
		getHandler().publish(new LogRecord(Level.FINE, s));
	}

	/**
	 * Publish a message to the logger, with a level of {@link Level#FINER}.
	 * 
	 * @param s the message to publish
	 */
	public final void finer(final String s) {
		getHandler().publish(new LogRecord(Level.FINER, s));
	}

	/**
	 * Publish a message to the logger, with a level of {@link Level#FINEST}.
	 * 
	 * @param s the message to publish
	 */
	public final void finest(final String s) {
		getHandler().publish(new LogRecord(Level.FINEST, s));
	}

	/**
	 * Publish a message to the logger, with a level of {@link Level#INFO}.
	 * 
	 * @param s the message to publish
	 */
	public final void info(final String s) {
		getHandler().publish(new LogRecord(Level.INFO, s));
	}

	/**
	 * Publish a message to the logger, with a level of {@link Level#SEVERE}.
	 * 
	 * @param s the message to publish
	 */
	public final void severe(final String s) {
		getHandler().publish(new LogRecord(Level.SEVERE, s));
	}

	/**
	 * Publish a message to the logger, with a level of {@link Level#WARNING}.
	 * 
	 * @param s the message to publish
	 */
	public final void warning(final String s) {
		getHandler().publish(new LogRecord(Level.WARNING, s));
	}

	/**
	 * Prints the stack trace of the provided throwable to the logger, with a
	 * level of {@link Level#SEVERE}.
	 * 
	 * @param throwable the exception to print
	 */
	public final void printStackTrace(final Throwable throwable) {
		severe("\n" + exceptionToString(throwable));
	}

	/**
	 * Converts an exception's stack trace to a new line separated string.
	 * 
	 * @param e the exception to convert to a string
	 * @return the converted string
	 */
	public static String exceptionToString(final Throwable e) {
		String s = e.toString();
		int i;
		for (i = 0; i < e.getStackTrace().length; i++) {
			s += "\n" + e.getStackTrace()[i];
		}
		return s;
	}

	/**
	 * Gets the last published message.
	 * 
	 * @return the message
	 */
	public final String getLastMessage() {
		return lastMessage;
	}

	/**
	 * Gets this logger's handler.
	 * 
	 * @return the handler
	 */
	private Handler getHandler() {
		return handler;
	}

	/**
	 * Closes this logger's streams.
	 */
	public final void close() {
		streamOut.close();
		if (fileOut != null) {
			fileOut.close();
		}
	}
}
