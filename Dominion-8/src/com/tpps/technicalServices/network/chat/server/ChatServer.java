package com.tpps.technicalServices.network.chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Scanner;

import com.tpps.technicalServices.network.Addresses;
import com.tpps.technicalServices.network.core.Server;

/**
 * This class represents one instance of the serverobject This serverobject is
 * able to read and interprete commands
 * 
 * @author jhuhn - Johannes Huhn
 */
public class ChatServer extends Server {

	public static int port = 1340;
	private ChatPacketHandler chatpackethandler;
	public static final SimpleDateFormat sdf = new SimpleDateFormat("<HH:mm:ss>: ");

	/**
	 * initializes the chatserver object
	 * 
	 * @throws IOException
	 * @author jhuhn - Johannes Huhn
	 */
	public ChatServer() throws IOException {
		super(new InetSocketAddress(Addresses.getLocalHost(), port), new ChatPacketHandler());
		this.chatpackethandler = (ChatPacketHandler) super.getHandler();
		this.chatpackethandler.setParent(this);
		this.chatpackethandler.init(this);
		this.setConsoleOutput();
	}

	/**
	 * This method puts a 'Dominion Chat Server' Banner to the console.
	 * Furthermore this method provides different commands to handle the
	 * chatserver Commands: - 'help' This command shows all avaible commands -
	 * 'create chatroom <nick1> <nick2> <nick3> <nick4>' This command create a
	 * chatroom with 4 different users - 'show all chatrooms' This command shows
	 * all used chatrooms on this server - 'delete chatroom <nickname>' This
	 * command deletes a chatromm by its ID or by one chatroom member
	 * 
	 * @author jhuhn - Johannes Huhn
	 */
	private void setConsoleOutput() {
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
			try {
				if (line.startsWith("help")) {
					System.out.println("help");
					System.out.println("create chatroom <nick1> <nick2> <nick3> <nick4>");
					System.out.println("show all chatrooms");
					System.out.println("delete chatroom <nickname>");
				} else if (line.startsWith("create chatroom")) {
					String[] words = line.split("\\s+");
					chatpackethandler.createChatRoom(words[2], words[3], words[4], words[5]);
				} else if (line.startsWith("show all chatrooms")) {
					for (Iterator<ChatRoom> iterator = this.chatpackethandler.getChatrooms().iterator(); iterator
							.hasNext();) {
						System.out.println(iterator.next());
					}
				} else if (line.startsWith("delete chatroom")) {
					String[] words = line.split("\\s+");
					boolean deletedRoom = false;
					try {
						int id = Integer.parseInt(words[2]);
						deletedRoom = this.chatpackethandler.deleteChatRoom(id);
					} catch (Exception e) {
						deletedRoom = this.chatpackethandler.deleteChatRoom(words[2]);
					}
					if (!deletedRoom) {
						System.out.println("Error while deleting a chatroom, command:" + line);
					} else {
						System.out.println("Deleted chatrooom successful");
					}
				} else {
					System.out.println("Bad command, Type in 'help' for commands");
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println("Bad Syntax, Type in 'help' for info");
				scanInput.close();
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
