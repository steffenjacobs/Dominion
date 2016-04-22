package com.tpps.technicalServices.network.chat.server;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.technicalServices.network.chat.packets.PacketSendAnswer;
import com.tpps.technicalServices.network.chat.packets.PacketSendChatAll;
import com.tpps.technicalServices.network.chat.packets.PacketSendChatCommand;
import com.tpps.technicalServices.network.chat.packets.PacketSendChatToClient;
import com.tpps.technicalServices.network.login.SQLHandling.SQLHandler;
import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;

/**
 * This class delivers all functionalities to run a global chat
 * 
 * @author jhuhn
 *
 */
public class GlobalChat {
	
	private final static String servercommand1 = "help";
	private final static String servercommand2 = "show all clients";
	private final static String servercommand3 = "show all ports";
	private final static String servercommand4 = "show all clients by ports";
	private final static String servercommand5 = "show statistic ";

	private ChatServer server;
	private ConcurrentHashMap<String, Integer> clientsByUsername = new ConcurrentHashMap<String, Integer>();

	/**
	 * initializes the global chat instance
	 * @param server the server object that belongs to the ChatPacketHandler
	 */
	public GlobalChat (ChatServer server){
		this.server =  server;
	}
	
	/**
	 * This method sends a chatmessage to all clients except the client who sent the message
	 * @param packet a packet that received from the ChatPacketHandler from a client
	 */
	public void sendChatToAllExceptSender(PacketSendChatAll packet){
		PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + packet.getUsername() + ": " + packet.getChatmessage());
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
		    String nickname = entry.getKey();
		    if(nickname.equals(packet.getUsername())){
		    	continue;
		    }  
		    try {
				this.server.sendMessage(entry.getValue(), answer);
			} catch (IOException e) {						
				e.printStackTrace();
				continue;
			}		   
		}
	}
	
	/**
	 * 
	 * @param packet
	 */
	public void sendChatToClient(PacketSendChatToClient packet){
		String receiver = packet.getReceiver().trim();
		if(!this.clientsByUsername.containsKey(receiver)){
			PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "The User '" + receiver + "' doesn't exist in global chat \n");
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			return;
		}		
		this.sendMessageToClient(packet.getSender(), receiver, packet.getMessage(), this.clientsByUsername.get(packet.getReceiver()));		
	}
	
	public void sendChatCommand(int port, PacketSendChatCommand packet){
		String msg = packet.getChatmessage();
		System.out.println("Chat Command: " + packet);
		
		if(!this.evaluateCommands(packet.getChatmessage(), packet.getSender(), port)){
			PacketSendAnswer answer2 = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "unknown command: " + msg + "\n");
			try {
				server.sendMessage(port, answer2);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	}
	
	
	
	private void sendMessageToClient(String sender, String receiver, String msg, int port){
		PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "PM from " + sender + ": " + msg + "\n");
		try {
			server.sendMessage(port, answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean evaluateCommands(String command, String sender, int port){
		
		switch(command.trim()){
		case servercommand1: //send answer packet back to user, with all comands servercommand1 == /help
			String allcomands = "Commands: \n/" + servercommand1 + "\n/" + servercommand2 + "\n/"
			+ servercommand3 + "\n/" + servercommand4 + "\n/" + servercommand5 + "<nickname> \n";
			PacketSendAnswer answer = new PacketSendAnswer(allcomands);
			try {
				server.sendMessage(port, answer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		case servercommand2: //show all clients
			StringBuffer buf = new StringBuffer("All connected clients: \n");
			Enumeration<String> clients = this.clientsByUsername.keys();
			while (clients.hasMoreElements()) {
				String user = clients.nextElement();				
				buf.append(user + "\n");												
			}
			PacketSendAnswer answer2 = new PacketSendAnswer(buf.toString());
			try {
				server.sendMessage(port, answer2);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			return true;
		case servercommand3: //show all ports
			StringBuffer buf2 = new StringBuffer("All connected ports: \n");
			Enumeration<Integer> ports = this.clientsByUsername.elements();			
			while (ports.hasMoreElements()) {
				int port2 = ports.nextElement();
				buf2.append(port2 + "\n");						
			}
			PacketSendAnswer answer3 = new PacketSendAnswer(buf2.toString());
			try {
				server.sendMessage(port, answer3);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			return true;
		case servercommand4://show all clients by ports
			StringBuffer buf3 = new StringBuffer("<client> : <port> \n");
			Enumeration<String> clients3 = this.clientsByUsername.keys();
			Enumeration<Integer> ports3 = this.clientsByUsername.elements();
			while (clients3.hasMoreElements()) {
				String user = clients3.nextElement();
				int port3 = ports3.nextElement();
				buf3.append(user + " : " + port3 + "\n");			
			}
			PacketSendAnswer answer4 = new PacketSendAnswer(buf3.toString());
			try {
				server.sendMessage(port, answer4);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			return true;			
		}
		//not in switch case, cause startsWith method is important
		if(command.trim().startsWith(servercommand5)){
			String[] split = command.trim().split("\\s+");
			System.out.println(split[2]);
			String nickname = split[2];
			
			SQLHandler.init("localhost", "3306", "root", "root", "accountmanager");
			SQLHandler.connect();
	
			String line = "";		
			ResultSet rs;
			try {
				rs = SQLStatisticsHandler.getStatisticsForPlayer(nickname);
				rs.next();
				String wins = "" + rs.getInt("wins");
				String losses = "" + rs.getInt("losses");
				String ratio = "" + rs.getDouble("win_loss");
				String totalMatches = "" + rs.getInt("games_played");
				String rank = "" + rs.getInt("rank");
				line = nickname + ": \n" + "	wins: " + wins + "\n	"
					+ "losses: " + losses + "\n	" + "ratio: " + ratio
					+ "\n	" + "total matches: " + totalMatches + "\n	"
					+ "rank: " + rank + "\n-------------------------\n";
			} catch (SQLException e1) {				
			//	e1.printStackTrace(); unknown user, resultset is empty
				line = "unknown  user: " + nickname; 
			}
			System.out.println(line);

			PacketSendAnswer answer5 = new PacketSendAnswer("User: " + line);
			try {
				this.server.sendMessage(port, answer5);
			} catch (IOException e) {			
				e.printStackTrace();
			}	
			SQLHandler.closeConnection();
			return true;
		}
		return false;
	}
	
	public void putUser(String name, int port){
		this.clientsByUsername.putIfAbsent(name, port);
	}
	
	public ConcurrentHashMap<String, Integer> getClientsByUsername() {
		return this.clientsByUsername;
	}
	
	public void removeUser(String user){
		this.clientsByUsername.remove(user);		
	}
}
