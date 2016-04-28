package com.tpps.technicalServices.logger;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import com.tpps.application.game.DominionController;
import com.tpps.technicalServices.network.gameSession.packets.PacketBroadcastLogSingleColor;
import com.tpps.technicalServices.util.ANSIUtil;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.ColorUtil;

/**
 * 
 * @author nicolaswipfler
 *
 */
public class GameLog {

	private static GameLogTextPane textPane;
	private static Map<Integer, Map<String, Color>> prepText;

	private static Color backgroundColor = Color.WHITE;
	private static Color timestampColor = ColorUtil.EPICBLUE;
	private static Color msgColor = Color.WHITE;

	@SuppressWarnings("unused")
	private static Color msgtypeColor = null;

	private static boolean ansiFlag = false;

	private static boolean guiPossible = !GraphicsEnvironment.isHeadless();

	private static boolean isInitialized = false;

	public static void init() {
		GameLog.isInitialized = true;
		String team = " Game Log\n";
		if (guiPossible) {
			GameLog.textPane = new GameLogTextPane();
		} else
			return;
		write(team, timestampColor, false);
		if (ansiFlag)
			writeToConsole(ANSIUtil.getCyanText(team + " Ready."));
		else
			writeToConsole(" Game Log\n" + "\n");
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

	// /**
	// * @return the msgtypeColor
	// */
	// public static Color getMsgtypeColor() {
	// return msgtypeColor;
	// }

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

	public static Map<Integer, Map<String, Color>> getPrepText() {
		return GameLog.prepText;
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
		try {
			DominionController.getInstance().getGameClient().sendMessage(new PacketBroadcastLogSingleColor(type, line, msgColor));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				write(msg, type.getColor(), type.getTimeStamp());
			}
		} else { // prevent Null Pointers
			init();
			log(type, line);
		}
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
	public static void logInGame(MsgType type, String line, Color color) {
		writeToConsole(line);
		if (isInitialized) {
			if (type.equals(MsgType.GAME) && guiPossible) {
				writeInGame(line, color);
			}
		} else { // prevent Null Pointers
			init();
			logInGame(type, line, color);
		}
	}

	/**
	 * write line to a JTextPane with or without a timestamp
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
	 * write line to a JTextPane without timestamp and custom color
	 * 
	 * @param line
	 *            the line to write on the JPanel
	 * @param textColor
	 *            the color of the text
	 * @param timestamp
	 *            determines whether the timestamp is written in front of the
	 *            line
	 */
	private static void writeInGame(String line, Color textColor) {
		GameLog.textPane.updateLogger(line, textColor);
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

	public static void appendToPrepText(int no, String text, Color color) {
		if (prepText == null) {
			prepText = new TreeMap<Integer, Map<String, Color>>();
		}
		prepText.put(no, CollectionsUtil.getTreeMap(text, color));
	}
}
