package com.tpps.technicalServices.logger;

import java.awt.Color;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.tpps.technicalServices.util.ANSIUtil;
import com.tpps.technicalServices.util.ColorUtil;

public class GameLog {

	private static final String time = "[" + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "]";
	
	private static LogUI logUI;
	/**
	 * Colors that can easily be changed for the UI Window
	 * */
	private static Color backgroundColor = Color.BLACK;
	private static Color timestampColor = Color.CYAN;
	private static Color msgColor = ColorUtil.MEDIUMGRAY;
	// unused:
	private static Color msgtypeColor = Color.blue;

	/**
	 * if the user has the ANSI plugin installed, set this flag to true so the console log will be colored 
	 *
	 * anyone can install the plugin with the following link:
	 * https://marketplace.eclipse.org/content/ansi-escape-console
	 * 
	 */
	private static boolean ansiPluginInstalled = true;
	
	/**
	 * determines if we want to use an extra UI for the log 
	 */
	private static boolean uiFlag = true;

	/**
	 * 
	 */
	static {
		init(uiFlag);
	}
	
	/**
	 * 
	 */
	public static void init(boolean ui) {
		String team = "GameLogger4Team++;\n\n";
		if (ui) {
			GameLog.logUI = new LogUI();
			/**
			 * writeUI with argument false is only intern for GameLogger class to write sth into the log without
			 * timestamp and user details
			 */
			writeUI(team, timestampColor, false);
		}		
		writeConsole(team);
	}

	/**
	 * @return the backgroundColor
	 */
	public static Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor the backgroundColor to set
	 */
	public static void setBackgroundColor(Color backgroundColor) {
		GameLog.backgroundColor = backgroundColor;
	}

	/**
	 * @return the timestampColor
	 */
	public static Color getTimestampColor() {
		return timestampColor;
	}

	/**
	 * @param timestampColor the timestampColor to set
	 */
	public static void setTimestampColor(Color timestampColor) {
		GameLog.timestampColor = timestampColor;
	}

	/**
	 * @return the msgtypeColor
	 */
	public static Color getMsgtypeColor() {
		return msgtypeColor;
	}

	/**
	 * @param msgtypeColor the msgtypeColor to set
	 */
	public static void setMsgtypeColor(Color msgtypeColor) {
		GameLog.msgtypeColor = msgtypeColor;
	}

	/**
	 * @return the msgColor
	 */
	public static Color getMsgColor() {
		return msgColor;
	}

	/**
	 * @param msgColor the msgColor to set
	 */
	public static void setMsgColor(Color msgColor) {
		GameLog.msgColor = msgColor;
	}

	/**
	 * @return the time
	 */
	public static String getTime() {
		return time;
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	private static String lineWithoutAnsi(MsgType type) {
		StringBuffer line = new StringBuffer();
		try {
			line.append(java.net.InetAddress.getLocalHost().getHostName() + ":~@" + System.getProperty("user.name") + " " + GameLog.time);
			line.append(" " + type.getSlang() + " > ");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return line.toString();
	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	private static String lineAnsi(MsgType type) {
		StringBuffer line = new StringBuffer();
		try {
			line.append(ANSIUtil.getCyanText(java.net.InetAddress.getLocalHost().getHostName() + ":~@" + System.getProperty("user.name") + " " + GameLog.time));
			line.append(" " + ANSIUtil.getRedText(type.getSlang()) + " > ");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return line.toString();
	}

	/**
	 * Panel
	 * 
	 * @param line
	 * @param isLog
	 */
	private static void writeUI(String line, Color textColor, boolean isLog) {
		GameLog.logUI.updateLogger(line, textColor, isLog);
	}

	/**
	 * Console
	 * 
	 * @param line
	 */
	private static void writeConsole(String line) {
		System.out.println(line);
	}

	/**
	 * text will be logged with user details and timestamp (by hard coded 'true' value)
	 * 
	 * @param type
	 * @param line
	 */
	public static void log(MsgType type, String line) {
		writeUI(lineWithoutAnsi(type) + line, type.getColor(), true);
		if (GameLog.ansiPluginInstalled)
			writeConsole(lineAnsi(type) + line);
		else
			writeConsole(lineWithoutAnsi(type) + line);
	}
	
	/**
	 * text will be logged without timestamp and user details
	 * 
	 * @param type
	 * @param line
	 */
	public static void log(MsgType type, String line, boolean isLog) {
		writeUI(lineWithoutAnsi(type) + line, type.getColor(), isLog);
		if (GameLog.ansiPluginInstalled)
			writeConsole(lineAnsi(type) + line);
		else
			writeConsole(lineWithoutAnsi(type) + line);
	}
}
