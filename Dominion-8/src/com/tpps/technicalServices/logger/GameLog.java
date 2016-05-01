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

	private static GameLogTextPane textPane;
	private static Map<Integer, Pair<String, Color>> prepText;

	private static Color backgroundColor = Color.WHITE;
	private static Color timestampColor = ColorUtil.EPICBLUE;
	private static Color msgColor = Color.WHITE;

	/**
	 * unused for now, see MsgType class for messageTypeColors
	 */
	@SuppressWarnings("unused")
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
	 * @param backgroundColor the backgroundColor to set
	 */
	public static void setBackgroundColor(Color backgroundColor) {
		GameLog.backgroundColor = backgroundColor;
	}

	/**
	 * @param msgtypeColor the msgtypeColor to set
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
	 * @param ansiFlag the ansiFlag to set
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
	 * @param guiPossible the guiPossible to set
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
	 * @param isInitialized the isInitialized to set
	 */
	public static void setInitialized(boolean isInitialized) {
		GameLog.isInitialized = isInitialized;
	}

	/**
	 * @param textPane the textPane to set
	 */
	public static void setTextPane(GameLogTextPane textPane) {
		GameLog.textPane = textPane;
	}

	/**
	 * @param prepText the prepText to set
	 */
	public static void setPrepText(Map<Integer, Pair<String, Color>> prepText) {
		GameLog.prepText = prepText;
	}

	/**
	 * @param timestampColor the timestampColor to set
	 */
	public static void setTimestampColor(Color timestampColor) {
		GameLog.timestampColor = timestampColor;
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
	 * @return the prepText
	 */
	public static Map<Integer, Pair<String, Color>> getPrepText() {
		return prepText;
	}

	/**
	 * @return the timestampColor
	 */
	public static Color getTimestampColor() {
		return timestampColor;
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
		if (guiPossible) 
			GameLog.textPane = new GameLogTextPane();
		else return;
		GameLog.log(MsgType.INIT, "Log", msgColor);
		GameLog.appendToPrepText(0,"Game Log\n", msgColor);
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
		if (ansiFlag)
			line.append(ANSIUtil.getCyanText(timestamp) + " " + ANSIUtil.getRedText(msg) + " > ");
		else
			line.append(timestamp + " " + msg + " > ");
		return line.toString();
	}

	/** Weder MsgType.getDisplay() noch MsgType.getTimeStamp() wird benutzt;
	 *  MsgType.setGameMode() ist auch �berfl�ssig.
	 *  
	 *  Es wird nur .GAME auf das textPane gelogt ohne timeStamp
	 *  Es wird nur !GAME in die Konsole gelogt mit timeStamp (und egal ob irgendwo steht type.getDisplay() ist false) 
	 *  
	 *  Die �bergebene Farbe ist nur relevant bei .GAME 
	 *  (ansonsten einfach MsgColor verwenden, ist in der Konsole eh schwarz
	 *  
	 *  newLines bei .GAME:         hinzuf�gen wann desired
	 *  newLines bei allen anderen: nicht hinzuf�gen, da es nicht auf das LogPane kommt und durch Syso eh eine newLine hat
	 * 
	 * 
	 * @param type the message type of the log message
	 * @param line the line to write
	 * @param color the color in which the line is displayed
	 */
	public static void log(MsgType type, String line, Color color) {
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
	}
	
	/**
	 * log a message with default color GameLog.getMsgColor()
	 * 
	 * @param type the message type of the log message
	 * @param color the color in which the line is displayed
	 */
	public static void log(MsgType type, String line) {
		GameLog.log(type, line, GameLog.getMsgColor());
	}

	/**
	 * append text in a specific Color to a TreeMap called "prepText" to collect 
	 * information about the game settings and log it (by calling logPrepText() in SGPH) as soon as the
	 * GameLogTextPane is ready.
	 * 
	 * @param no position in which the text will be put in the log
	 * @param text the text to log
	 * @param color the color in which the text will be displayed
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