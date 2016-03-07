package com.tpps.application.network.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;

import com.tpps.application.network.core.Server;
import com.tpps.application.network.login.SQLHandling.Password;
import com.tpps.application.network.login.SQLHandling.SQLHandler;
import com.tpps.application.network.login.SQLHandling.SQLOperations;
import com.tpps.application.network.login.SQLHandling.SQLStatisticsHandler;
import com.tpps.application.network.login.SQLHandling.SQLType;
import com.tpps.application.network.login.SQLHandling.Statistic;

public class ChatServer extends Server{
	
	public static String domain = "127.0.0.1";
	public static int port = 1340;
	private ChatPacketHandler chatpackethandler;

	public ChatServer() throws IOException {
		super(new InetSocketAddress(domain, port), new ChatPacketHandler());
		((ChatPacketHandler)super.getHandler()).setServer(this);
		this.chatpackethandler = (ChatPacketHandler) super.getHandler();
		this.setConsoleOutput();		
	}
	
	private void setConsoleOutput(){
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Chat Server - Team ++; * * * * *");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println();
		System.out.println("Enter 'help' to see all available commands.");
		System.out.println();
		
		String line = null;
		Scanner scanInput = new Scanner(System.in);
		while (true) {
			line = scanInput.nextLine();
				if (line.startsWith("help")) {
					System.out.println("help");
					System.out.println("create chatroom <nick1> <nick2> <nick3> <nick4>");
				}
				else if(line.startsWith("create chatroom")){
					String[] words = line.split("\\s+");
					for (int i = 0; i < words.length; i++) {
						System.out.println(words[i]);
					}
					chatpackethandler.createChatRoom(words[2], words[3], words[4], words[5]);
				}
				
		}
	}

	public static void main(String[] args) {
		try {
			new ChatServer();
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
}
