package com.tpps.technicalServices.logger;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import com.tpps.technicalServices.util.ANSIUtil;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.ColorUtil;

import javafx.util.Pair;

/**
 * 
 * @author nicolaswipfler
 *
 */
public class GameLog {

	// private static final boolean DEBUG_FLAG = false;

	private static GameLogTextPane textPane;
	private static Map<Integer, Pair<String, Color>> prepText;

	private static Color backgroundColor = Color.BLACK;
	private static Color timestampPanelColor = ColorUtil.EPICBLUE;
	private static String timestampConsoleColor = ANSIUtil.ANSI_WHITE;
	private static Color msgColor = Color.WHITE;

	// private static int alreadyLogged;
	// private static int count;
	//
	// private static HashMap<Integer, LogObject> waitingLogs;
	//
	// public static int getCountAndInc() {
	// System.out.println(ANSIUtil.getRedText("<<<<<<<<<<< COUNT CALLED, value: "
	// + GameLog.count));
	// return GameLog.count++;
	// }
	//
	// public static int getAlreadyLogged() {
	// return GameLog.alreadyLogged;
	// }
	//
	// public static HashMap<Integer,LogObject> getWaitingLogs() {
	// return GameLog.waitingLogs;
	// }

	// public static void log(MsgType type, String line, int count, Color color)
	// {
	// System.out.println(">>>> in the countLogMethod");
	// System.out.println(">>>> alreadyLogged: " + alreadyLogged);
	// System.out.println(">>>> count: " + count + ", GameLog.count: " +
	// GameLog.count);
	// if (count - 1 == GameLog.alreadyLogged) {
	// System.out.println(">>>> in the if");
	// GameLog.log(type, line, color);
	// GameLog.alreadyLogged++;
	// } else if (GameLog.waitingLogs.get(count) != null) {
	// System.out.println(">>>> in the elseIf");
	// GameLog.log(GameLog.waitingLogs.get(count).getType(),
	// GameLog.waitingLogs.get(count).getLine(),
	// GameLog.waitingLogs.get(count).getColor());
	// GameLog.alreadyLogged++;
	// GameLog.waitingLogs.remove(count);
	// } else {
	// System.out.println(">>>> in the else");
	// GameLog.waitingLogs.put(count, new LogObject(type, line, color));
	// }
	// }

	/**
	 * unused for now, see MsgType class for messageTypeColors
	 */
	private static Color msgTypeColor = null;

	/**
	 * if the user has the ANSI plugin installed, set this flag to true so the
	 * console log will be colored
	 *
	 * anyone can install the plugin with the following link:
	 * https://marketplace.eclipse.org/content/ansi-escape-console
	 * 
	 */
	private static boolean ansiFlag = true;

	/**
	 * determines if there will be an extra window for the log besides the
	 * console. guiPossible: is the device is able to display a gui?
	 * iWantAJFrame: do I want to have an extra JFrame for this?
	 */
	private static boolean guiPossible = !GraphicsEnvironment.isHeadless();

	/**
	 * determines wheter the textPane is already initialized so there won't be a
	 * Null-Pointer
	 */
	private static boolean isInitialized = false;

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
	 * @param msgtypeColor
	 *            the msgtypeColor to set
	 */
	public static void setMsgtypeColor(Color msgtypeColor) {
		GameLog.msgTypeColor = msgtypeColor;
	}

	/**
	 * @return the ansiFlag
	 */
	public static boolean isAnsiFlag() {
		return ansiFlag;
	}

	/**
	 * @param ansiFlag
	 *            the ansiFlag to set
	 */
	public static void setAnsiFlag(boolean ansiFlag) {
		GameLog.ansiFlag = ansiFlag;
	}

	/**
	 * @return the guiPossible
	 */
	public static boolean isGuiPossible() {
		return guiPossible;
	}

	/**
	 * @param guiPossible
	 *            the guiPossible to set
	 */
	public static void setGuiPossible(boolean guiPossible) {
		GameLog.guiPossible = guiPossible;
	}

	/**
	 * @return the isInitialized
	 */
	public static boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * @param isInitialized
	 *            the isInitialized to set
	 */
	public static void setInitialized(boolean isInitialized) {
		GameLog.isInitialized = isInitialized;
	}

	/**
	 * @param textPane
	 *            the textPane to set
	 */
	public static void setTextPane(GameLogTextPane textPane) {
		GameLog.textPane = textPane;
	}

	/**
	 * @param prepText
	 *            the prepText to set
	 */
	public static void setPrepText(Map<Integer, Pair<String, Color>> prepText) {
		GameLog.prepText = prepText;
	}

	/**
	 * @return the msgColor
	 */
	public static Color getMsgColor() {
		return msgColor;
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
	 * @return the prepText
	 */
	public static Map<Integer, Pair<String, Color>> getPrepText() {
		return prepText;
	}
	
	/**
	 * @return the timestampPanelColor
	 */
	public static Color getTimestampPanelColor() {
		return timestampPanelColor;
	}

	/**
	 * @param timestampPanelColor the timestampPanelColor to set
	 */
	public static void setTimestampPanelColor(Color timestampPanelColor) {
		GameLog.timestampPanelColor = timestampPanelColor;
	}

	/**
	 * @return the timestampConsoleColor
	 */
	public static String getTimestampConsoleColor() {
		return timestampConsoleColor;
	}

	/**
	 * @param timestampConsoleColor the timestampConsoleColor to set
	 */
	public static void setTimestampConsoleColor(String timestampConsoleColor) {
		GameLog.timestampConsoleColor = timestampConsoleColor;
	}

	/**
	 * @return the msgTypeColor
	 */
	public static Color getMsgTypeColor() {
		return msgTypeColor;
	}

	/**
	 * @param msgTypeColor the msgTypeColor to set
	 */
	public static void setMsgTypeColor(Color msgTypeColor) {
		GameLog.msgTypeColor = msgTypeColor;
	}

	/* ---------- !new Logic ---------- */
	
	/**
	 * initialization method which is called in the beginning writes the team
	 * name first: GameLogger4Team++;
	 * 
	 * and after that an INIT message with "GameLogger initialized"
	 */
	public static void init() {
		GameLog.isInitialized = true;
//		GameLog.count = 1;
//		GameLog.alreadyLogged = 0;
//		GameLog.waitingLogs = new HashMap<Integer, LogObject>();
		if (guiPossible)
			GameLog.textPane = new GameLogTextPane();
		else
			return;
		GameLog.log(MsgType.INIT, "Log", msgColor);
		GameLog.appendToPrepText(0, "Game Log\n", msgColor);
		if (prepText == null)
			GameLog.prepText = new TreeMap<Integer, Pair<String, Color>>();
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
	private static String createTimestamp(MsgType type) {
		StringBuffer line = new StringBuffer();
		String timestamp = "[" + new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()) + "]";
		String msg = type.getMessage();
		line.append((ansiFlag ? ANSIUtil.getAnsiColoredText(timestamp, GameLog.timestampConsoleColor) 
				+ " " + ANSIUtil.getAnsiColoredText(msg, type.getAnsiColor()) 
				: timestamp + " " + msg) + " > ");
		return line.toString();
	}

	/**
	 * Weder MsgType.getDisplay() noch MsgType.getTimeStamp() wird benutzt;
	 * MsgType.setGameMode() ist auch ueberfluessig.
	 * 
	 * Es wird nur .GAME auf das textPane gelogt ohne timeStamp Es wird nur
	 * !GAME in die Konsole gelogt mit timeStamp (und egal ob irgendwo steht
	 * type.getDisplay() ist false)
	 * 
	 * Die uebergebene Farbe ist nur relevant bei .GAME (ansonsten einfach
	 * MsgColor verwenden, ist in der Konsole eh schwarz
	 * 
	 * newLines bei .GAME: hinzufuegen wann desired newLines bei allen anderen:
	 * nicht hinzufuegen, da es nicht auf das LogPane kommt und durch Syso eh
	 * eine newLine hat
	 * 
	 * 
	 * @param type
	 *            the message type of the log message
	 * @param line
	 *            the line to write
	 * @param color
	 *            the color in which the line is displayed
	 */
	/*private*/public static void log(MsgType type, String line, Color color) {
		// if (!DEBUG_FLAG) {
		if (isInitialized) {
			if (type.equals(MsgType.GAME) && guiPossible) {
				GameLog.textPane.updateTextArea(line, color);
			}
			if (!type.equals(MsgType.GAME))
				System.out.println(createTimestamp(type) + line);
		} else {
			init();
			log(type, line, color);
		}
		// }
	}

	/**
	 * log a message with default color GameLog.getMsgColor()
	 * 
	 * @param type
	 *            the message type of the log message
	 * @param color
	 *            the color in which the line is displayed
	 */
	public static void log(MsgType type, String line) {
		GameLog.log(type, line, GameLog.getMsgColor());
	}

	/**
	 * append text in a specific Color to a TreeMap called "prepText" to collect
	 * information about the game settings and log it (by calling logPrepText()
	 * in SGPH) as soon as the GameLogTextPane is ready.
	 * 
	 * @param no
	 *            position in which the text will be put in the log
	 * @param text
	 *            the text to log
	 * @param color
	 *            the color in which the text will be displayed
	 */
	public static void appendToPrepText(int no, String text, Color color) {
		if (prepText == null) {
			GameLog.prepText = new TreeMap<Integer, Pair<String, Color>>();
		}
		GameLog.prepText.put(no, CollectionsUtil.getPair(text, color));
	}

	/**
	 * reset the prepText to a new instance of TreeMap<etc..>
	 */
	public static void resetPrepText() {
		GameLog.prepText = new TreeMap<Integer, Pair<String, Color>>();
	}
}