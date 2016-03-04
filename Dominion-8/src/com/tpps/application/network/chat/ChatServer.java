package com.tpps.application.network.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.Server;

public class ChatServer extends Server{
	
	public static String domain = "127.0.0.1";
	public static int port = 1340;

	public ChatServer(SocketAddress address, PacketHandler _handler) throws IOException {
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

}
