package com.tpps.technicalServices.logger;

import java.awt.Color;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.tpps.technicalServices.util.ANSIUtil;

public class GameLog {

	private static final String time = "[" + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "]";
	
	private static LogUI logUI;
	/**
	 * Colors that can easily be changed for the UI Window
	 * */
	private static Color backgroundColor = Color.black;
	private static Color timestampColor = Color.yellow;
	private static Color msgtypeColor = Color.green;
	private static Color msgColor = new Color(215,215,215);

	/**
	 * if the user has the ANSI plugin installed, set this flag to true so the console log will be colored 
	 */
	private static boolean ansiPluginInstalled = false;

	/**
	 * 
	 */
	static {
		init(true);
	}
	
	/**
	 * 
	 */
	public static void init(boolean ui) {
		String team = "GameLogger4Team++;\n\n";
		if (ui) {
			GameLog.logUI = new LogUI();
			writeUI(team, false);
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
	private static void writeUI(String line, boolean isLog) {
		GameLog.logUI.updateLogger(line, isLog);
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
	 * 
	 * @param type
	 * @param line
	 */
	public static void log(MsgType type, String line) {
		writeUI(lineWithoutAnsi(type) + line, true);
		if (GameLog.ansiPluginInstalled)
			writeConsole(lineAnsi(type) + line);
		else
			writeConsole(lineWithoutAnsi(type) + line);
	}
}
