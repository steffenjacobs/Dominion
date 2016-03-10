package com.tpps.technicalServices.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GameLog {

	private static final String logfileDir = System.getProperty("user.dir") + "/src/resources/logfiles/";
	private static final String filename = "MyLogger.txt";
	private static final File logfile = new File(logfileDir + "/" + filename);

	private static final String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

	private static PrintWriter out;

	public static void init() {
		try {
			out = new PrintWriter(new FileWriter(logfile), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String prefix(MsgType type) {
		StringBuffer line = new StringBuffer().append(time);
		switch (type) {
		case INIT:
			line.append(" [INI] ");
			break;
		case INFO:
			line.append(" [INF] ");
			break;
		case DEBUG:
			line.append(" [BUG] ");
			break;
		case EXCEPTION:
			line.append(" [EXC] ");
		case GAME:
			line.append(" [GAM] ");
			break;
		default: // unhandled
			break;
		}
		return line.toString();
	}

	public static void log(MsgType type, Exception e) {
		// hier dann e.printStackTrace() oder das google e.printFUllStack oder wie es heißt
	}

	public static void log(MsgType type, String line) {
		write(prefix(type) + line);
	}

	private static void write(String line) {
		if (out != null) {
			out.write(line);
			out.flush();
		} else {
			try {
				out = new PrintWriter(new FileWriter(logfile), true);
				GameLog.log(MsgType.DEBUG, "PrintWriter not initialized");
			} catch (IOException e) {
				// GameLog.log(MsgType.EXCEPTION, e);
			}
		}
	}

	public static void main(String[] args) {
//		GameLog.init();
		GameLog.log(MsgType.INFO, "Test ob das alles so funktioniert");
		out.close();
	}
}
