package com.tpps.technicalServices.logger;

import java.awt.Color;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.tpps.technicalServices.util.ANSIUtil;
import com.tpps.technicalServices.util.ColorUtil;

public class GameLog {

	/**
	 * How-To:
	 * 
	 * call GameLog.init()
	 * get the TextPane with GameLog.getTextPane();
	 * add this textPane to GameWindow
	 * add GameLog.log(MsgType.GAME, "XYZ"); to every Game relevant action
	 * 
	 * update comments in the following 4 classes: GameLog, GameLogUI, GameLogTextPane, MsgType
	 * 
	 * */
	
	private static GameLogTextPane textPane;

	/**
	 * Colors that can easily be changed for the UI Window
	 */
	private static Color backgroundColor = Color.BLACK;
	private static Color timestampColor = Color.CYAN;
	private static Color msgColor = ColorUtil.MEDIUMGRAY;
	
	/**
	 * @unused, for messageTypeColors see MsgType class
	 */
	private static Color msgtypeColor = null;

	/**
	 * if the user has the ANSI plugin installed, set this flag to true so the
	 * console log will be colored
	 *
	 * anyone can install the plugin with the following link:
	 * https://marketplace.eclipse.org/content/ansi-escape-console
	 * 
	 */
	private static boolean ansiFlag = false;

	/**
	 * determines if there will be an extra window for the log besides the
	 * console. 
	 * guiPossible: is the device is able to display a gui?
	 * iWantAJFrame: do I want to have an extra JFrame for this? 
	 */
	// private static boolean guiPossible = !GraphicsEnvironment.isHeadless();
	// private static boolean iWantAJFrame = false;

	/**
	 * determines wheter the textPane is already initialized so there won't be a Null-Pointer
	 * */
	private static boolean isInitialized = false;

	/**
	 * initialization method which is called in the beginning
	 * writes the team name first:
	 * GameLogger4Team++;
	 * 
	 * and after that an INIT message with "GameLogger initialized"
	 */
	public static void init() {
		GameLog.isInitialized = true;
		String team = "GameLogger4Team++;\n\n";
		/*if (displayUIWindow) {
			 create UI if desired 
		}*/
		GameLog.textPane = new GameLogTextPane();
		write(team, timestampColor, false);
 		if (ansiFlag)
			writeToConsole(ANSIUtil.getCyanText(team));
		else
			writeToConsole(team);
		GameLog.log(MsgType.INIT, "GameLogger initialized");
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
	 * @return the textPane
	 */
	public static GameLogTextPane getTextPane() {
		return textPane;
	}

	/**
	 * @param textPane the textPane to set
	 */
	public static void setTextPane(GameLogTextPane textPane) {
		GameLog.textPane = textPane;
	}
	
	/**
	 * 
	 * @param type the messageType of the log message
	 * @param ansi determines whether the line shall have ANSI codes or not
	 * @return the computed line with hostname, username, timestamp, messagetype and the actual 
	 * message with(out) ANSI codes;
	 */
	private static String computeLine(MsgType type, boolean ansi) {
		StringBuffer line = new StringBuffer();
		try {
			String timestamp = java.net.InetAddress.getLocalHost().getHostName() + ":~@"
					+ System.getProperty("user.name") + " " + "["
					+ new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "]";
			String msgtype = type.getSlang();
			if (ansiFlag && ansi)
				line.append(ANSIUtil.getCyanText(timestamp) + " " + ANSIUtil.getRedText(msgtype) + " > ");
			else
				line.append(timestamp + " " + msgtype + " > ");
		} catch (UnknownHostException e) {
			GameLog.log(MsgType.EXCEPTION, e.getMessage());
		}
		return line.toString();
	}
	
	/**
	 * log the message with message type to the ui and console (if GameLog.uiFlag is true)
	 * 
	 * @param type the message type of the message to log
	 * @param line the line to log
	 */
	public static void log(MsgType type, String line) {
		if (isInitialized) {
			if (type.getDisplay()) {
				writeToConsole(computeLine(type, true) + line);
				// if (guiPossible) {
				write(computeLine(type, false) + line, type.getColor(), type.getTimeStamp());
				// }
			}
		} else { // prevent Null Pointers
			init();
		}
	}
	
	/**
	 * write to a JPanel of LogUI
	 * 
	 * writeUI with argument false is only intern for GameLogger class
	 * to write sth into the log without timestamp and user details	
	 * 
	 * @param line the line to write on the JPanel
	 * @param textColor the color of the text
	 * @param timestamp determines whether the timestamp is written in front of the line
	 */
	private static void write(String line, Color textColor, boolean timestamp) {
		GameLog.textPane.updateLogger(line, textColor, timestamp);
	}

	/**
	 * write to the console
	 * 
	 * @param line the line to write to the console
	 */
	private static void writeToConsole(String line) {
		System.out.println(line);
	}
}
