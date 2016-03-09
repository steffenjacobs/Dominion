package com.tpps.application.network.chat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ChatRoom {

	private ChatServer server;
	private ConcurrentHashMap<String, Integer> clientsByUsername = new ConcurrentHashMap<String, Integer>();
	private int id;
	private static int idcounter = 1;
	
	private final static String servercommand1 = "help";
	private final static String servercommand2 = "show all clients";
	private final static String servercommand3 = "show all ports";
	private final static String servercommand4 = "show all clients by ports";
	private final static String servercommand5 = "votekick";
	private boolean kill = false;
	
	private Timer timer;
	private int seconds = 45;
	
	private Votekick votekick;
	
	public ChatRoom(ConcurrentHashMap<String, Integer> clientsByUser, ChatServer server){
		this.clientsByUsername = clientsByUser;
		this.server = server;
		this.id = idcounter++;
	}
	
	public void sendChatToAll(PacketSendChatAll packet){
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
	
	public void sendChatToClient(PacketSendChatToClient packet){
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
	
	public void sendToSpecificClient(String sender, PacketSendAnswer answer){
		int port = this.clientsByUsername.get(sender);
		try {
			this.server.sendMessage(port, answer);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	
	
	public void evaluateCommand(PacketSendChatCommand packet){
		this.kill = false;
		if(packet.getChatmessage().startsWith("votekick ")){
			if(this.votekick != null){
				PacketSendAnswer answer6 = new PacketSendAnswer("There is an active vote currently");
				try {
					this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer6);
				} catch (IOException e) {				
					e.printStackTrace();
				}
			}else{
				try{	//sets up the votekick
					String[] words = packet.getChatmessage().split("\\s+");
					ArrayList<String> notvoted = this.getClients();
					notvoted.remove(packet.getSender());
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
			            		System.out.println("Timer zu ende :D");
			            		ChatRoom.this.sendMEssageToAll("Der Votekick ist vorbei");
			            		this.cancel();
			            		t.cancel();
			            		//evaluate vote
			            		ChatRoom.this.evaluateVotekick();
			            		kill = true;
			            		ChatRoom.this.votekick = null;
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
		}else if(packet.getChatmessage().startsWith("vote ")){
			if(this.votekick == null){
				PacketSendAnswer answer = new PacketSendAnswer("Derzeit läuft keine Abstimmung");
				String sender = packet.getSender();
				this.sendToSpecificClient(sender, answer);
			}		
			
			if(this.votekick.checkIfUserVoted(packet.getSender())){
				PacketSendAnswer answer = new PacketSendAnswer("Du hast schon abgestimmt");
				String sender = packet.getSender();
				this.sendToSpecificClient(sender, answer);
				return;
			}
			
			if(packet.getChatmessage().startsWith("vote y")){
				this.votekick.addVote(packet.getSender(), true);
				PacketSendAnswer answer = new PacketSendAnswer("You voted successfully");
				String sender = packet.getSender();
				this.sendToSpecificClient(sender, answer);
			}else if(packet.getChatmessage().startsWith("vote n")){
				this.votekick.addVote(packet.getSender(), false);
				PacketSendAnswer answer = new PacketSendAnswer("You voted successfully");
				String sender = packet.getSender();
				this.sendToSpecificClient(sender, answer);
			}else{
				PacketSendAnswer answer = new PacketSendAnswer("Vote wird nicht gewertet, [y/n] ist erlaubt");
				String sender = packet.getSender();
				this.sendToSpecificClient(sender, answer);
			}
			kill = true;
		}
		
		if(kill){
			return;
		}
		
		
		switch(packet.getChatmessage()){
		case servercommand1: 
			String msg = "Commands: \n/" + servercommand1 + "\n/" + servercommand2 + "\n/" + servercommand3 + "\n/" + servercommand4;
			PacketSendAnswer answer = new PacketSendAnswer(msg);
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			break;
		case servercommand2:
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
			break;
		case servercommand3:
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
			break;
		case servercommand4: 
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
		System.out.println(this.votekick.printResults());
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
}
