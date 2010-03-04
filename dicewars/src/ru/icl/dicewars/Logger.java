package ru.icl.dicewars;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Logger {
	public static void init(){
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger("");
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord record) {
				Level level = record.getLevel();
				StringBuilder sb = new StringBuilder();
				Date time = new Date(record.getMillis());
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
				sb.append(simpleDateFormat.format(time) + " ");
				
				if (level.equals(Level.INFO)){
					sb.append("[INFO] ");
				}
				
				if (level.equals(Level.WARNING)){
					sb.append("[WARN] ");
				}
				sb.append(record.getMessage()+"\n");
				return sb.toString();
			}
		});
		for (Handler h : logger.getHandlers()){
			logger.removeHandler(h);
		}
		logger.addHandler(ch);
	}
}
