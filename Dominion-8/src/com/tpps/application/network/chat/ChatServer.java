package com.tpps.application.network.chat;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.tpps.application.network.core.Server;

public class ChatServer extends Server{
	
	public static String domain = "127.0.0.1";
	public static int port = 1340;

	public ChatServer() throws IOException {
		super(new InetSocketAddress(domain, port), new ChatPacketHandler());
		((ChatPacketHandler)super.getHandler()).setServer(this);
		this.setConsoleOutput();
	}
	
	private void setConsoleOutput(){
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("* * * * * Dominion Chat Server - Team ++; * * * * *");
		System.out.println("      * * * * * * * * * * * * * * * * * * * *");
		System.out.println("            * * * * * * * * * * * * * *      ");
		System.out.println();
	//	System.out.println("Enter 'help' to see all available commands.");
		System.out.println();
	}

	public static void main(String[] args) {
		try {
			new ChatServer();
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
}
