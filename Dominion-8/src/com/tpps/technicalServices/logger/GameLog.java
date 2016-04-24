package com.tpps.technicalServices.logger;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.tpps.technicalServices.util.ANSIUtil;
import com.tpps.technicalServices.util.ColorUtil;

/**
 * 
 * @author nicolaswipfler
 *
 */
public class GameLog {

	private static GameLogTextPane textPane;

	/**
	 * Colors that can easily be changed for the UI Window
	 */
	private static Color backgroundColor = Color.WHITE;
	private static Color timestampColor = ColorUtil.EPICBLUE;
	private static Color msgColor = Color.BLACK;

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
	 * console. guiPossible: is the device is able to display a gui?
	 * iWantAJFrame: do I want to have an extra JFrame for this?
	 */
	private static boolean guiPossible = !GraphicsEnvironment.isHeadless();
	// private static boolean iWantAJFrame = false;

	/**
	 * determines wheter the textPane is already initialized so there won't be a
	 * Null-Pointer
	 */
	private static boolean isInitialized = false;

	/**
	 * initialization method which is called in the beginning writes the team
	 * name first: GameLogger4Team++;
	 * 
	 * and after that an INIT message with "GameLogger initialized"
	 */
	public static void init() {
		GameLog.isInitialized = true;
		String team = "Game Log\n";
		if (guiPossible) {
			GameLog.textPane = new GameLogTextPane();
		} else
			return;
		write(team, timestampColor, false);
		if (ansiFlag)
			writeToConsole(ANSIUtil.getCyanText(team + " Ready."));
		else
			writeToConsole(team + "\n");
		GameLog.log(MsgType.INIT, "GameLogger initialized");
	}

	/**
	 * @return the backgroundColor
	 */
	public static Color getBackgroundColor() {
		return backgroundColor;
	}

	/**
	 * @param backgroundColor
	 *            the backgroundColor to set
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
	 * @param timestampColor
	 *            the timestampColor to set
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
	 * @param msgtypeColor
	 *            the msgtypeColor to set
	 */
	public static void setMsgtypeColor(Color msgtypeColor) {
		GameLog.msgtypeColor = msgtypeColor;
	}

	/**
	 * @return the msgColor
	 */
	public static Color getMsgColor() {
		return GameLog.msgColor;
	}

	/**
	 * @param msgColor
	 *            the msgColor to set
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
	 * @param textPane
	 *            the textPane to set
	 */
	public static void setTextPane(GameLogTextPane textPane) {
		GameLog.textPane = textPane;
	}

	/**
	 * 
	 * @param type
	 *            the messageType of the log message
	 * @param ansi
	 *            determines whether the line shall have ANSI codes or not
	 * @return the created line with hostname, username, timestamp, messagetype
	 *         and the actual message with(out) ANSI codes;
	 */
	private static String createTimestamp(MsgType type, boolean ansi) {
		StringBuffer line = new StringBuffer();
		String timestamp = "[" + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "]";
		String msgtype = type.getMessage();
		if (ansiFlag && ansi)
			line.append(ANSIUtil.getCyanText(timestamp) + " " + ANSIUtil.getRedText(msgtype) + " > ");
		else
			line.append(timestamp + " " + msgtype + " > ");
		return line.toString();
	}

	
	public static void broadcastMessage(MsgType type, String line) {
//		try {
//			DominionController.getInstance().getGameClient().sendMessage(new PacketBroadcastLog(type, line, msgColor));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * log the message with message type to the ui (if GameLog.guiPossible is
	 * true) and console
	 *
	 * @param type
	 *            the message type of the message to log
	 * @param line
	 *            the line to log
	 */
	public static void log(MsgType type, String line) {
		String msg = type.getTimeStamp() ? createTimestamp(type, true) + line : line;
		writeToConsole(msg);
		if (isInitialized) {
			if (type.getDisplay() && guiPossible) {

				/**
				 * die folgende Zeile wuerde vor jeden GameLog.log(MsgType.GAME,
				 * ""); den Namen des aktuellen Spielers setzen da man aber evtl
				 * schreiben will "--- Nico's Turn ---" und nicht Nico: ---
				 * Nico's Turn --- denke ich es ist besser das immer von Hand
				 * davor zu schreiben String msg = type.equals(MsgType.GAME) ?
				 * GameServer
				 * .getInstance().getGameController().getActivePlayerName() +
				 * ": " : "";
				 * 
				 * String msg = type.getTimeStamp() ? createTimestamp(type,
				 * true) + line : line; writeToConsole(msg);
				 */

				write(msg, type.getColor(), type.getTimeStamp());
			}
		} else { // prevent Null Pointers
			init();
		}
	}

	/**
	 * write to a JPanel
	 * 
	 * write with argument false is only intern for GameLogger class to write
	 * sth into the log without timestamp and user details
	 * 
	 * @param line
	 *            the line to write on the JPanel
	 * @param textColor
	 *            the color of the text
	 * @param timestamp
	 *            determines whether the timestamp is written in front of the
	 *            line
	 */
	private static void write(String line, Color textColor, boolean timestamp) {
		GameLog.textPane.updateLogger(line, textColor, timestamp);
	}

	/**
	 * write to the console
	 * 
	 * @param line
	 *            the line to write to the console
	 */
	private static void writeToConsole(String line) {
		System.out.println(line);
	}
}
