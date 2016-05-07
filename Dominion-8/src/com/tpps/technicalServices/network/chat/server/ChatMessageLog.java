package com.tpps.technicalServices.network.chat.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class provides functionalities to log all chatmessages
 * 
 * @author jhuhn
 */
public class ChatMessageLog {
	
	private static final String filePath = "chatlog.txt";
//	private static final String filePath = System.getProperty("user.home") + "\\chatlog.txt";
	private static BufferedWriter writer;
	private static File logFile = new File(filePath);
	
	/**
	 * This method opens the chatlog file
	 * 
	 * @author jhuhn
	 */
	public static void loadLogFile(){
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
	 * This method inserts a single chatmessage to the logfile
	 * 
	 * @author jhuhn
	 * @param timeStamp
	 *            String of the time
	 * @param user
	 *            String of the user who
	 * @param line
	 *            String of a chatline
	 */
	public static void insertChatLineToLog(String timeStamp, String user, String line){
		try {
			writer.write(timeStamp + ": " + user + ": " + line + "\n");
			writer.newLine();
			writer.flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}	
	
	public static void main(String[] args) {
		ChatMessageLog.loadLogFile();
		ChatMessageLog.insertChatLineToLog("[awdadw]", "kevinS", "Hello world all of this retards");
		ChatMessageLog.insertChatLineToLog("[awdadw]", "kevinS", "YOLO NIOGGA");
		ChatMessageLog.insertChatLineToLog("[awdadw]", "kevinS", "HAHA WAS LOS");
		for (int i = 0; i < 1000; i++) {
			ChatMessageLog.insertChatLineToLog("[awdadw]", "kevinS", "test " + i);
		}
	}
}
