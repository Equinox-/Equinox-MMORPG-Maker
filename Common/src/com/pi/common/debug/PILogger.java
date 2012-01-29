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

public class PILogger {
    String lastMessage = "Loading...";
    private final Level level = Level.ALL;
    private final Handler handler;
    private final Formatter formatter;
    public boolean showDate = false, showTime = false, showCode = false,
	    showLevel = true;
    public PrintStream streamOut;
    private PrintStream errorStream;
    private PrintStream fileOut;

    public PILogger(File f, PrintStream streamOutt) {
	if (f != null) {
	    try {
		if (!f.exists())
		    f.createNewFile();
		if (f.exists())
		    fileOut = new PrintStream(new FileOutputStream(f));
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	this.streamOut = streamOutt;
	this.handler = getHandler();
	this.formatter = getFormatter();
	errorStream = new PrintStream(streamOut);
    }

    public PILogger(PrintStream out) {
	this(null, out);
    }

    public void fine(String s) {
	getHandler().publish(new LogRecord(Level.FINE, s));
    }

    public void finer(String s) {
	getHandler().publish(new LogRecord(Level.FINER, s));
    }

    public void finest(String s) {
	getHandler().publish(new LogRecord(Level.FINEST, s));
    }

    public void info(String s) {
	getHandler().publish(new LogRecord(Level.INFO, s));
    }

    public void severe(String s) {
	getHandler().publish(new LogRecord(Level.SEVERE, s));
    }

    public void warning(String s) {
	getHandler().publish(new LogRecord(Level.WARNING, s));
    }

    public static String exceptionToString(Exception e) {
	String s = e.toString();
	int i;
	for (i = 0; i < Math.min(e.getStackTrace().length, 10); i++) {
	    s += "\n" + e.getStackTrace()[i];
	}
	if (i < e.getStackTrace().length - 1) {
	    s += "\n...";
	}
	return s;
    }

    public PrintStream getErrorStream() {
	return errorStream;
    }

    public String getLastMessage() {
	return lastMessage;
    }

    private Handler getHandler() {
	if (this.handler == null) {
	    return new Handler() {
		@Override
		public void close() throws SecurityException {
		}

		@Override
		public void flush() {
		}

		@Override
		public void publish(LogRecord record) {
		    String s = getFormatter() != null ? getFormatter().format(
			    record) : ("[" + record.getLevel() + "] " + record
			    .getMessage());
		    if (record.getLevel().intValue() >= level.intValue()) {
			lastMessage = record.getMessage();
			streamOut.println(s);
			if (fileOut != null)
			    fileOut.println(s);
		    }
		}
	    };
	} else {
	    return handler;
	}
    }

    private Formatter getFormatter() {
	if (formatter == null) {
	    return new Formatter() {
		Date dat = new Date();
		DateFormat dForm = DateFormat.getDateInstance();
		DateFormat tForm = DateFormat.getTimeInstance();

		@Override
		public String format(LogRecord record) {
		    dat.setTime(record.getMillis());
		    String source;
		    if (record.getSourceClassName() != null) {
			source = record.getSourceClassName();
			if (record.getSourceMethodName() != null) {
			    source += ":" + record.getSourceMethodName();
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
		    if (showDate)
			s += date + " ";
		    if (showTime)
			s += time + " ";
		    if (showCode)
			s += source + " ";
		    if (showLevel)
			s += "[" + record.getLevel().getName() + "] ";
		    s += message;
		    if (!throwable.equals(""))
			s += "\n" + throwable;
		    return s;
		}
	    };
	} else {
	    return formatter;
	}
    }

    public void close() {
	errorStream.close();
	streamOut.close();
	if (fileOut != null)
	    fileOut.close();
    }
}
