package com.tpps.technicalServices.logger;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

import com.tpps.technicalServices.util.ANSIUtil;
import com.tpps.technicalServices.util.CollectionsUtil;
import com.tpps.technicalServices.util.ColorUtil;

import javafx.util.Pair;

/**
 * all messages we want to log can be logged with methods of this class
 * replaces System.out in some ways
 * 
 * is also used for ingame logging of the game actions
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

	private static ConcurrentSkipListMap<Long, LogObject> waitingLogs;
	private static Timer timer;
	
	private static final String filePath = "gamelog.txt";
	private static BufferedWriter writer;
	private static File logFile = new File(filePath);

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
	 * @param timestampPanelColor
	 *            the timestampPanelColor to set
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
	 * @param timestampConsoleColor
	 *            the timestampConsoleColor to set
	 */
	public static void setTimestampConsoleColor(String timestampConsoleColor) {
		GameLog.timestampConsoleColor = timestampConsoleColor;
	}

	/**
	 * @return the waitingLogs
	 */
	public static ConcurrentSkipListMap<Long, LogObject> getWaitingLogs() {
		return waitingLogs;
	}

	/**
	 * @param waitingLogs
	 *            the waitingLogs to set
	 */
	public static void setWaitingLogs(ConcurrentSkipListMap<Long, LogObject> waitingLogs) {
		GameLog.waitingLogs = waitingLogs;
	}

	/**
	 * @return the timer
	 */
	public static Timer getTimer() {
		return timer;
	}

	/**
	 * @param timer
	 *            the timer to set
	 */
	public static void setTimer(Timer timer) {
		GameLog.timer = timer;
	}

	/**
	 * initialization method which is called in the beginning writes the team
	 * name first: GameLogger4Team++;
	 * 
	 * and after that an INIT message with "GameLogger initialized"
	 */
	public static void init() {
		GameLog.isInitialized = true;
		GameLog.waitingLogs = new ConcurrentSkipListMap<Long, LogObject>();
		GameLog.timer = new Timer();
//		GameLog.timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				for (long elementTime : GameLog.waitingLogs.keySet()) {
//					long currentTime = System.currentTimeMillis();
//					if (currentTime - elementTime > 300) {
//						GameLog.log(GameLog.waitingLogs.remove(elementTime));
//					}
//				}
//			}
//		}, 0, 2000);
		GameLog.loadLogFile();
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
		line.append((ansiFlag ? ANSIUtil.getAnsiColoredText(timestamp, GameLog.timestampConsoleColor) + " " + ANSIUtil.getAnsiColoredText(msg, type.getAnsiColor()) : timestamp + " " + msg) + " > ");
		return line.toString();
	}

	/**
	 * 
	 * @param type
	 *            the messageType of the log message
	 * @param line
	 *            the line to log
	 * @param timestamp
	 *            a timestamp to sort the messages
	 * @param color
	 *            a logColor
	 */
	protected static void log(MsgType type, String line, long timestamp, Color color) {
		GameLog.waitingLogs.put(timestamp, new LogObject(type, line, color));
	}

	/**
	 * calls the local log (MsgType, String, Color)
	 * 
	 * @param logO the logObject to log
	 */
	protected static void log(LogObject logO) {
		GameLog.log(logO.getType(), logO.getLine(), logO.getColor());
	}

	/**
	 * log the message to the console if its not of type GAME and if the system
	 * is able to display a GUI, log it to that GUI if its of type GAME
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
			if (!type.equals(MsgType.GAME)) {
				String msg = createTimestamp(type) + line;
				System.out.println(msg);
				GameLog.insertLineToLogFile(msg);
			}
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
	 * @param line
	 *            the line to log
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
	
	/**
	 * This method opens the gamelog file
	 * 
	 */
	private static void loadLogFile(){
		try {			
			if(!logFile.exists()){
				logFile.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(logFile));
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * inserts a single message to the logfile
	 * 
	 * @param timeStamp
	 *            String of the time
	 * @param user
	 *            String of the user who
	 * @param line
	 *            String of a chatline
	 */
	private static void insertLineToLogFile(String msg){
		try {
			writer.write(msg);
			writer.newLine();
			writer.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}	
}