package com.pi.common;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.*;

public class PILogger {
    String lastMessage = "Loading...";
    private final Level level = Level.ALL;
    private final Handler handler;
    private final Formatter formatter;
    public boolean showDate = false, showTime = false, showCode = false,
	    showLevel = true;
    public PrintStream streamOut = System.out;

    public PILogger(PrintStream streamOut) {
	this.streamOut = streamOut;
	this.handler = getHandler();
	this.formatter = getFormatter();
    }

    public PILogger() {
	this.handler = getHandler();
	this.formatter = getFormatter();
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
			if (record.getLevel().intValue() == Level.SEVERE
				.intValue() || record.getThrown() != null)
			    streamOut.println(s);
			streamOut.println(s);
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
}
