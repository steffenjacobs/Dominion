package com.tpps.application.network.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {

	private ChatServer server;
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
	
	public ChatRoom(ConcurrentHashMap<String, Integer> clientsByUser, ChatServer server){
		this.clientsByUsername = clientsByUser;
		this.server = server;
		this.id = idcounter++;
	}
	
	public void sendChatToAllExceptSender(PacketSendChatAll packet){
		String message = packet.getChatmessage();
		String sender = packet.getUsername();
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
		    String nickname = entry.getKey();
		    if(nickname.equals(sender)){
		    	continue;
		    }
		    int port = entry.getValue();
		    PacketSendAnswer answer = new PacketSendAnswer(message);
		    try {
				server.sendMessage(port, answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	public void handleVote(PacketChatVote packet){
		if(votekick.getNotvotedyet().contains(packet.getSender())){
			votekick.getNotvotedyet().add(packet.getSender());
			votekick.addVote(packet.getSender(), packet.getVoted());
		}else{
			PacketSendAnswer answer = new PacketSendAnswer("You voted already");
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	public void sendChatToChatRoomClient(PacketSendChatToClient packet){
		String sender = packet.getSender();
		String receiver = packet.getReceiver();
		String message = packet.getMessage();
		
		int port = this.clientsByUsername.get(receiver);
		PacketSendAnswer answer = new PacketSendAnswer("Message from " + sender + ": " + message);
		try {
			server.sendMessage(port, answer);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	public void sendChatToChatRoomClient(String sender, PacketSendAnswer answer){
		int port = this.clientsByUsername.get(sender);
		try {
			this.server.sendMessage(port, answer);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
		
	public void evaluateCommand(PacketSendChatCommand packet){		
		if(packet.getChatmessage().startsWith("votekick ")){
			if(this.votekick != null){
				PacketSendAnswer answer6 = new PacketSendAnswer("There is an active vote currently");
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
	
	private void evaluateCommand5(PacketSendChatCommand packet){
		try{	//sets up the votekick
			String[] words = packet.getChatmessage().split("\\s+");
			ArrayList<String> notvoted = this.getClients();
			notvoted.remove(packet.getSender());
			//TODO: is words[1](user to get kicked) really a username ?
			this.votekick = new Votekick(notvoted, words[1], packet.getSender());					
			
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
	            		ChatRoom.this.sendMEssageToAll("Noch " + second + " Sekunden!");
	            	}else 
	            	if(second <= 0){
	            		System.out.println("Votekick zu ende");	            		
	            		this.cancel();
	            		t.cancel();
	            		//evaluate vote
	            		ChatRoom.this.evaluateVotekick();	            		
	            	}
	            }   
	        },0, 1000);
	         //----------------------
			
		}catch(ArrayIndexOutOfBoundsException e){
			PacketSendAnswer answer7 = new PacketSendAnswer("Wrong command " + packet.getChatmessage());
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer7);
			} catch (IOException e1) {					
				e1.printStackTrace();
			}
		}

	}
	
	private void evaluateCommand6(PacketSendChatCommand packet){
		if(this.votekick == null){
			PacketSendAnswer answer = new PacketSendAnswer("Derzeit läuft keine Abstimmung");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}		
		
		if(this.votekick.checkIfUserVoted(packet.getSender())){
			PacketSendAnswer answer = new PacketSendAnswer("Du hast schon abgestimmt");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
			return;
		}
		
		if(packet.getChatmessage().startsWith("vote y")){
			this.votekick.addVote(packet.getSender(), true);
			PacketSendAnswer answer = new PacketSendAnswer("You voted successfully");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}else if(packet.getChatmessage().startsWith("vote n")){
			this.votekick.addVote(packet.getSender(), false);
			PacketSendAnswer answer = new PacketSendAnswer("You voted successfully");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}else{
			PacketSendAnswer answer = new PacketSendAnswer("Vote wird nicht gewertet, [y/n] ist erlaubt");
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}
	}
	
	private void evaluateCommand7(PacketSendChatCommand packet){
		if(this.votekickresults == null){
			PacketSendAnswer answerx = new PacketSendAnswer("There are no votekick results");
			this.sendChatToChatRoomClient(packet.getSender(), answerx);
		}else{
			PacketSendAnswer answerx = new PacketSendAnswer(this.votekickresults);
			this.sendChatToChatRoomClient(packet.getSender(), answerx);
		}
	}
	
	
	public void sendMEssageToAll(String msg){
		PacketSendAnswer answer = new PacketSendAnswer(msg);
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
			int port = entry.getValue();
			try {
				this.server.sendMessage(port, answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	public void evaluateVotekick(){		
		this.votekickresults = this.votekick.printResults();
		System.out.println(this.votekickresults);
		ChatRoom.this.votekick = null;
		ChatRoom.this.sendMEssageToAll("Der Votekick ist vorbei.");//TODO: say clients userxy will be kicked or not
																	//TODO: execute the kick
	}
	
	
	public ArrayList<String> getClients() {
		ArrayList<String> clientsInChatRoom = new ArrayList<String>();
		Enumeration<String> clients = this.clientsByUsername.keys();
		while(clients.hasMoreElements()){
			clientsInChatRoom.add(clients.nextElement());
		}
		return clientsInChatRoom;
	}
	
	public int getId() {
		return id;
	}
	
	public String toString(){
		Iterator<String> members = this.getClients().iterator();
		String membersAsString = "";
		while(members.hasNext()){
			membersAsString += members.next() + ", ";
		}
		return "ID: " + this.id + " Members: " + membersAsString;
	}
	
	public ConcurrentHashMap<String, Integer> getClientsByUsername() {
		return clientsByUsername;
	}
}
