package com.tpps.technicalServices.logger;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import javafx.util.Pair;

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
	private static Map<Integer, Pair<String, Color>> prepText;

	private static Color backgroundColor = Color.BLACK;
	private static Color timestampPanelColor = ColorUtil.EPICBLUE;
	private static String timestampConsoleColor = ANSIUtil.ANSI_WHITE;
	private static Color msgColor = Color.WHITE;
	
	private static TreeMap<Long, LogObject> waitingLogs;
	private static Timer timer;

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

	/**
	 * initialization method which is called in the beginning writes the team
	 * name first: GameLogger4Team++;
	 * 
	 * and after that an INIT message with "GameLogger initialized"
	 */
	public static void init() {
		GameLog.isInitialized = true;
		GameLog.waitingLogs = new TreeMap<Long, LogObject>();
		GameLog.timer = new Timer();
		GameLog.timer.schedule(new TimerTask(){
            @Override
            public void run() {            		
            	for (long elementTime : GameLog.waitingLogs.keySet()) {
            		long currentTime = System.currentTimeMillis();
            		System.out.println("I am here and the current time is " + currentTime + ", diff. is " + (currentTime-elementTime));
            		if (currentTime - elementTime > 300) {
            			GameLog.log(GameLog.waitingLogs.remove(elementTime));
            		}
            	}
            }   
        },0, 1000);
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

	public static void log(MsgType type, String line, long timestamp, Color color) {
		System.out.println(">>> here and timestamp is " + timestamp);
		GameLog.waitingLogs.put(timestamp, new LogObject(type,line,color));
	}
	
	private static void log(LogObject logO) {
		GameLog.log(logO.getType(), logO.getLine(), logO.getColor());
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