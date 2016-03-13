package com.tpps.application.network.chat.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.application.network.chat.packets.PacketChatVote;
import com.tpps.application.network.chat.packets.PacketSendAnswer;
import com.tpps.application.network.chat.packets.PacketSendChatAll;
import com.tpps.application.network.chat.packets.PacketSendChatCommand;
import com.tpps.application.network.chat.packets.PacketSendChatToClient;

/**
 * This class delivers all functionalities to run a chatroom
 * @author jhuhn - Johannes Huhn
 */
public class ChatRoom {

	private ChatServer server;
	private ChatPacketHandler chatpackethandler;
	private ConcurrentHashMap<String, Integer> clientsByUsername = new ConcurrentHashMap<String, Integer>();
	private int id;
	private static int idcounter = 1;
	
	private final static String servercommand1 = "help";
	private final static String servercommand2 = "show all clients";
	private final static String servercommand3 = "show all ports";
	private final static String servercommand4 = "show all clients by ports";
	private final static String servercommand5 = "votekick <nickname>";
	private final static String servercommand6 = "vote [y/n] only use in a active vote";
	private final static String servercommand7 = "show votekickresults";
	
	private String votekickresults;	
	private Votekick votekick;
	
	/**
	 * initializes the ChatRoom object
	 * @param clientsByUser a ConcuttentHashMap that handle all clients and ports for this chatroom object
	 * @param server the serverobject which is important to send packets
	 */
	public ChatRoom(ConcurrentHashMap<String, Integer> clientsByUser, ChatServer server, ChatPacketHandler chatpackethandler){
		this.clientsByUsername = clientsByUser;
		this.server = server;
		this.id = idcounter++;
		this.chatpackethandler = chatpackethandler;
	}
	
	/**
	 * This method sends a packet to all clients in a chatroom except the user that sent the packet,
	 * used for public chat in chatroom
	 * @param packet a packet that received the server from a user (public chat)
	 */	
	public void sendChatToAllExceptSender(PacketSendChatAll packet){
		String message = packet.getChatmessage();
		String sender = packet.getUsername();
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
		    String nickname = entry.getKey();
		    if(nickname.equals(sender)){
		    	continue;
		    }
		    int port = entry.getValue();
		    PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + sender + ": " + message);
		    try {
				server.sendMessage(port, answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * this method either counts a vote from a user or sends a message back that he already voited
	 * @param packet the packet that received to cast a vote
	 */
	public void handleVote(PacketChatVote packet){
		if(votekick.getNotvotedyet().contains(packet.getSender())){
			votekick.getNotvotedyet().add(packet.getSender());
			votekick.addVote(packet.getSender(), packet.getVoted());
		}else{
			PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "You voted already");
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method is responsible to send a private message to a client
	 * @param packet the packet that received the server. it contains a private message
	 */
	public void sendChatToChatRoomClient(PacketSendChatToClient packet){
		String sender = packet.getSender();
		String receiver = packet.getReceiver().trim();
		String message = packet.getMessage();
		
		if(!this.clientsByUsername.containsKey(receiver)){
			PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "The User '" + receiver + "' doesn't exist in this chatroom" );
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			return;
		}
		
		int port = this.clientsByUsername.get(receiver);
		PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "PM from " + sender + ": " + message);
		try {
			server.sendMessage(port, answer);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is responsible to send a message back to a client
	 * @param sender a String representation of the nickname who sent the message
	 * @param answer the packet which receive the receiver (including the message)
	 */
	public void sendChatToChatRoomClient(String sender, PacketSendAnswer answer){
		int port = this.clientsByUsername.get(sender);
		try {
			this.server.sendMessage(port, answer);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * 	This method evaluates commands and passes to the right method
	 * @param packet the packet that received the server from a client
	 */
	public void evaluateCommand(PacketSendChatCommand packet){		
		if(packet.getChatmessage().startsWith("votekick ")){
			if(this.votekick != null){
				PacketSendAnswer answer6 = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "There is an active vote currently");
				try {
					this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer6);
				} catch (IOException e) {				
					e.printStackTrace();
				}
			}else{
				this.evaluateCommand5(packet);									
			}
			return;
		}else if(packet.getChatmessage().startsWith("vote ")){
			this.evaluateCommand6(packet);
			return;
		}
		
		switch(packet.getChatmessage()){
		case servercommand1: 
			this.evaluateCommand1(packet);
			break;
		case servercommand2:
			this.evaluateCommand2(packet);
			break;
		case servercommand3:
			this.evaluateCommand3(packet);
			break;
		case servercommand4: 
			this.evaluateCommand4(packet);
			break;
		case servercommand7:
			this.evaluateCommand7(packet);
			break;
		default:
			PacketSendAnswer answer5 = new PacketSendAnswer("Wrong command: " + packet.getChatmessage());
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer5);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			break;
		}		
	}
	
	/**
	 * executes the help command
	 * @param packet the packet that received the server from a client
	 */
	private void evaluateCommand1(PacketSendChatCommand packet){
		String msg = "Commands: \n/" + servercommand1 + "\n/"
				+ servercommand2 + "\n/" + servercommand3 + "\n/"
				+ servercommand4 + "\n/" + servercommand5 + "\n/"
				+ servercommand6 + "\n/" + servercommand7 + "\n";
		PacketSendAnswer answer = new PacketSendAnswer(msg);
		try {
			this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * executes the 'show all clients' command
	 * @param packet the packet that received the server from a client
	 */
	private void evaluateCommand2(PacketSendChatCommand packet){
		Enumeration<String> clients = this.clientsByUsername.keys();
		String msg2 = "All connected Clients in this chatroom: \n";
		while(clients.hasMoreElements()){
			msg2 += clients.nextElement() + "\n";
		}
		PacketSendAnswer answer2 = new PacketSendAnswer(msg2);
		try {
			this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer2);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * executes the 'show all ports' command
	 * @param packet the packet that received the server from a client
	 */
	private void evaluateCommand3(PacketSendChatCommand packet){
		Enumeration<Integer> ports = this.clientsByUsername.elements();
		String msg3 = "All connected ports in this chatroom: \n";
		while(ports.hasMoreElements()){
			msg3 += ports.nextElement() + "\n";
		}
		PacketSendAnswer answer3 = new PacketSendAnswer(msg3);
		try {
			this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer3);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * executes the 'show all client and ports' command. This command combines servercommand3 & servercommand2
	 * @param packet the packet that received the server from a client
	 */
	private void evaluateCommand4(PacketSendChatCommand packet){
		Enumeration<Integer> ports2 = this.clientsByUsername.elements();
		Enumeration<String> clients2 = this.clientsByUsername.keys();
		String msg4 = "All connected clients with ports in this chatroom: \n";
		while(ports2.hasMoreElements()){
			msg4 += clients2.nextElement() + "  :  " + ports2.nextElement() + "\n";
		}
		PacketSendAnswer answer4 = new PacketSendAnswer(msg4);
		try {
			this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer4);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * sets up the votekick command
	 * @param packet the packet that received the server from a client
	 */
	private void evaluateCommand5(PacketSendChatCommand packet){
		try{	//sets up the votekick
			String[] words = packet.getChatmessage().split("\\s+");
			ArrayList<String> notvoted = this.getClients();
			notvoted.remove(packet.getSender());
			
			if(!clientsByUsername.containsKey(words[1])){
				this.sendChatToChatRoomClient(packet.getSender(), new PacketSendAnswer("The Client '" + words[1] + "' doesn't exist in this chatroom") );
				return;
			}
			
			this.votekick = new Votekick(notvoted, words[1], packet.getSender());					
			this.sendMessageToAll("Do you want to kick '" + words[1] + "' vote with '/vote [y/n]'  30 seconds to go"  );
			//------------Timer----------
			Timer t = new Timer();
			t.schedule(new TimerTask(){
	            int second = 31;
	            @Override
	            public void run() {
	            	second--;
	            	//if(second % 5 == 0 && second != 0){
	            	if(second == 5  && second != 0){
	            		System.out.println("Noch " + second + " Sekunden");
	            		ChatRoom.this.sendMessageToAll(second + " seconds to go!");
	            	}else 
	            	if(second <= 0){
	            		System.out.println("Votekick ends");	            		
	            		this.cancel();
	            		t.cancel();
	            		//evaluate vote
	            		ChatRoom.this.evaluateVotekick();	            		
	            	}
	            }   
	        },0, 1000);
	         //----------------------
			
		}catch(ArrayIndexOutOfBoundsException e){
			PacketSendAnswer answer7 = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "Wrong command " + packet.getChatmessage());
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer7);
			} catch (IOException e1) {					
				e1.printStackTrace();
			}
		}

	}

	/**
	 * evaluate the '/vote ' command
	 * @param packet the packet that received the server from a client
	 */
	private void evaluateCommand6(PacketSendChatCommand packet){
		if(this.votekick == null){
			PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "There is currently no vote");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}		
		
		if(this.votekick.checkIfUserVoted(packet.getSender())){
			PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "You voted already");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
			return;
		}
		
		if(packet.getChatmessage().startsWith("vote y")){
			this.votekick.addVote(packet.getSender(), true);
			PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "You voted successfully");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}else if(packet.getChatmessage().startsWith("vote n")){
			this.votekick.addVote(packet.getSender(), false);
			PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "You voted successfully");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}else{
			PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "Your vote command failed, [y/n] is avaible ");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}
	}
	
	/**
	 * executes the votekickresults command
	 * @param packet the packet that received the server from a client
	 */
	private void evaluateCommand7(PacketSendChatCommand packet){
		if(this.votekickresults == null){
			PacketSendAnswer answerx = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "There are no votekick results");
			this.sendChatToChatRoomClient(packet.getSender(), answerx);
		}else{
			PacketSendAnswer answerx = new PacketSendAnswer(this.votekickresults);
			this.sendChatToChatRoomClient(packet.getSender(), answerx);
		}
	}
	
	/**
	 * This methods sends a message to all clients in the chatroom
	 * @param msg a String representation of text message to send
	 */
	public void sendMessageToAll(String msg){
		String message = ChatServer.sdf.format(new Date().getTime()) + msg;
		PacketSendAnswer answer = new PacketSendAnswer(message);
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
			int port = entry.getValue();
			try {
				this.server.sendMessage(port, answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method evaluates the votekick, if necessary the player gets kicked
	 */
	public void evaluateVotekick(){
		String getkicked = this.votekick.getUsertogetkicked();
		if(this.votekick.fastEvaluateVote()){			
			this.chatpackethandler.kickPlayer(getkicked);
			this.sendMessageToAll("The player '" + getkicked + "' gets kicked!");			
		}else{
			this.sendMessageToAll("The player '" + getkicked + "' stays in the match!");
		}
		this.votekickresults = this.votekick.printResults();
		System.out.println(this.votekickresults);
		ChatRoom.this.votekick = null; 		
	}
	
	/**
	 * gets all clients in the chatroom
	 * @return an Arraylist of Strings with all clients that are connected in the chatroom
	 */
	public ArrayList<String> getClients() {
		ArrayList<String> clientsInChatRoom = new ArrayList<String>();
		Enumeration<String> clients = this.clientsByUsername.keys();
		while(clients.hasMoreElements()){
			clientsInChatRoom.add(clients.nextElement());
		}
		return clientsInChatRoom;
	}
	
	/**
	 * 
	 * @return the chatroom id as an Integer
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * overrides the toString() method to get a useful String representation of the chatroom
	 */
	@Override
	public String toString(){
		Iterator<String> members = this.getClients().iterator();
		String membersAsString = "";
		while(members.hasNext()){
			membersAsString += members.next() + ", ";
		}
		return "ID: " + this.id + " Members: " + membersAsString;
	}
	
	/**
	 * 
	 * @return the ConcurrentHashMap included all clients by its ports
	 * 	Key: String: username
	 * Object: Integer: port
	 */
	public ConcurrentHashMap<String, Integer> getClientsByUsername() {
		return clientsByUsername;
	}
}
