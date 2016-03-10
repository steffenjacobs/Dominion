package com.tpps.application.network.chat.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

import com.tpps.application.network.chat.packets.PacketChatHandshake;
import com.tpps.application.network.chat.packets.PacketSendAnswer;
import com.tpps.application.network.chat.packets.PacketSendChatAll;
import com.tpps.application.network.chat.packets.PacketSendChatCommand;
import com.tpps.application.network.chat.packets.PacketSendChatToClient;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;

/**
 * This class represents one client for chat purposes.
 * This class is responsilbe for sending messages/chatpackets to the chatserver
 * @author jhuhn - Johannes Huhn
 */
public class ChatClient extends PacketHandler{

	private Client chatclient;
	private String sender;
	
	/**
	 * initializes the chat client object, sends a handshake packet to the chatserver
	 * @param username a String representation of the nickname
	 */
	public ChatClient(String username) {
		this.sender = username;
		try {
			chatclient = new Client(new InetSocketAddress("127.0.0.1", 1340), this, false);
			PacketChatHandshake handshake = new PacketChatHandshake(sender);
			chatclient.sendMessage(handshake);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * this method is called, when the client gets a packet from the chatserver.
	 * It displays a message on the gui
	 */
	@Override
	public void handleReceivedPacket(int port, final Packet packet) {
		switch(packet.getType()){
		case SEND_CHAT_ANSWER:
			PacketSendAnswer answer = (PacketSendAnswer) packet;
			System.out.println(answer.getAnswer());
			break;
		default:System.out.println("sth with answer packet is wrong"); break;
		}	
	}

	/**
	 * This method is responsible for sending packets to the chatserver.
	 * The chatmessage to send will be parsed and embedded in the right packet.
	 * if the chatmessage starts with..
	 * ... /votekick : the client starts a vote to kick a player
	 * .../	: the client executes a chatcommand
	 * ...@	:the client sends a private message to one player
	 * If the chatmessage starts not in the given template, the chatmessage goes to globalchat / chatroom	
	 * @param chatmessage a String representation of a command or text message
	 */
	public void sendMessage(String chatmessage){
		if(chatmessage.startsWith("/votekick ")){
			this.sendCommand(new StringBuffer(chatmessage).deleteCharAt(0).toString());
		}
		else if(chatmessage.startsWith("/")){
			this.sendCommand(new StringBuffer(chatmessage).deleteCharAt(0).toString());
		}
		else if(chatmessage.startsWith("@")){
			this.sendMessageToClient(new StringBuffer(chatmessage).deleteCharAt(0).toString());
		}else{
			PacketSendChatAll packet = new PacketSendChatAll(sender, chatmessage);
			try {
				chatclient.sendMessage(packet);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method parses the chatmessage in important tokens and sends it to the chatserver
	 * e.g: "@player2 this is a message for player2"
	 * @param message a String representation of the chatmessage which goes to one specific client
	 */
	private void sendMessageToClient(String message){
		String[] split = message.split(" ");
		String receiver = split[0];
		message = "";
		for (int i = 1; i < split.length; i++) {
			message += split[i] + " ";
		}
		
		PacketSendChatToClient packet = new PacketSendChatToClient(this.sender, message, receiver);
		try {
			chatclient.sendMessage(packet);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method embeds the command in a packet and sends it to the chat server
	 * @param command a String representation of the used command
	 */
	private void sendCommand(String command){
		PacketSendChatCommand packet = new PacketSendChatCommand(sender, command);
		try {
			chatclient.sendMessage(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ChatClient c = new ChatClient("steffen");
		System.out.println("I am: " + c.sender);
		Scanner scanInput = new Scanner(System.in);
		String line = null;
		while(true){
			line = scanInput.nextLine();
			c.sendMessage(line);
		}
	}
}
