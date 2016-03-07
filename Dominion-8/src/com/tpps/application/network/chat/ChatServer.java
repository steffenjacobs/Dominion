package com.tpps.application.network.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Scanner;

import com.tpps.application.network.core.Server;

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
			try{
				if (line.startsWith("help")) {
					System.out.println("help");
					System.out.println("create chatroom <nick1> <nick2> <nick3> <nick4>");
					System.out.println("show all chatrooms");
					System.out.println("delete chatroom <nickname>");
				}
				else if(line.startsWith("create chatroom")){
					String[] words = line.split("\\s+");
					for (int i = 0; i < words.length; i++) {
						System.out.println(words[i]);
					}
					chatpackethandler.createChatRoom(words[2], words[3], words[4], words[5]);
				}else if(line.startsWith("show all chatrooms")){
					for (Iterator<ChatRoom> iterator = this.chatpackethandler.getChatrooms().iterator(); iterator.hasNext();) {
						System.out.println(iterator.next());						
					}
				}else if(line.startsWith("delete chatroom")){
					String[] words = line.split("\\s+");
					boolean deletedRoom = false;
					try{
						int id = Integer.parseInt(words[2]);
						deletedRoom = this.chatpackethandler.deleteChatRoom(id);
					}catch(Exception e){
						deletedRoom = this.chatpackethandler.deleteChatRoom(words[2]);
					}
					if(!deletedRoom){
						System.out.println("Error while deleting a chatroom, command:" + line);
					}else{
						System.out.println("Deleted chatrooom successful");
					}
				}else{
					System.out.println("Bad command, Type in 'help' for commands");
				}
			}catch(ArrayIndexOutOfBoundsException e){
				System.out.println("Bad Syntax, Type in 'help' for info");
			}
		}
	//	scanInput.close();
	}

	public static void main(String[] args) {
		try {
			new ChatServer();
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
}
