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
	
	private final static String help_servercommand1 = "help";
	private final static String showClients_servercommand2 = "show all clients";
	private final static String showPorts_servercommand3 = "show all ports";
	private final static String showClientsAndPorts_servercommand4 = "show all clients by ports";
	private final static String statistic_servercommand5 = "show statistic ";

	private ChatServer server;
	private ConcurrentHashMap<String, Integer> clientsByUsername = new ConcurrentHashMap<String, Integer>();

	/**
	 * initializes the global chat instance
	 * 
	 * @author jhuhn
	 * @param server
	 *            the server object that belongs to the ChatPacketHandler
	 */
	public GlobalChat (ChatServer server){
		this.server =  server;
	}
	
	/**
	 * This method sends a chatmessage to all clients except the client who sent
	 * the message
	 * 
	 * @author jhuhn
	 * @param packet
	 *            a packet that received from the ChatPacketHandler from a
	 *            client
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
	 * This method checks if the user can send a PM to a client. If yes, this
	 * method calls the method which executes the PM
	 * 
	 * @author jhuhn
	 * @param packet
	 *            a packet that received from the ChatPacketHandler from a
	 *            client
	 */
	public void sendPMToClient(PacketSendChatToClient packet){
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
		this.sendMessageToSpecificClient(packet.getSender(), receiver, packet.getMessage(), this.clientsByUsername.get(packet.getReceiver()));		
	}
	
	/**
	 * This method mainly calls the 'evaluateCommands' method and handles the
	 * state of command
	 * 
	 * @author jhuhn
	 * @param port
	 *            Integer representation of the client port who sent the command
	 * @param packet
	 *            a packet that received from the ChatPacketHandler from a
	 *            client
	 */
	public void sendChatCommand(int port, PacketSendChatCommand packet){
		String msg = packet.getChatcommand();
		System.out.println("Chat Command: " + packet);
		
		if(!this.evaluateCommands(packet.getChatcommand(), packet.getSender(), port)){
			PacketSendAnswer answer2 = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "unknown command: " + msg + "\n");
			try {
				server.sendMessage(port, answer2);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * This method sends a chatmessage to a specific client
	 * 
	 * @author jhuhn
	 * @param sender
	 *            String representation of the client, who want to send the PM
	 * @param receiver
	 *            String representation of the client, who should receive the PM
	 * @param message
	 *            String representation of the message
	 * @param port
	 *            Integer representation of the clients port, who receives the
	 *            PM
	 */
	private void sendMessageToSpecificClient(String sender, String receiver, String message, int port){
		PacketSendAnswer answer = new PacketSendAnswer(ChatServer.sdf.format(new Date().getTime()) + "PM from " + sender + ": " + message + "\n");
		try {
			server.sendMessage(port, answer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method evaluates all chatcommands that are supported in the global
	 * chat
	 * 
	 * @author jhuhn
	 * @param command
	 *            String representation of the command who sent by the user
	 * @param sender
	 *            String representation of the sender
	 * @param port
	 *            Integer representation of the users port
	 * @return true if the cammand executed successful, false else
	 */
	private boolean evaluateCommands(String command, String sender, int port){		
		switch(command.trim()){
		case help_servercommand1: //send answer packet back to user, with all comands servercommand1 == /help
			String allcomands = "Commands: \n/" + help_servercommand1 + "\n/" + showClients_servercommand2 + "\n/"
			+ showPorts_servercommand3 + "\n/" + showClientsAndPorts_servercommand4 + "\n/" + statistic_servercommand5 + "<nickname> \n";
			PacketSendAnswer answer = new PacketSendAnswer(allcomands);
			try {
				server.sendMessage(port, answer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		case showClients_servercommand2: //show all clients
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
		case showPorts_servercommand3: //show all ports
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
		case showClientsAndPorts_servercommand4://show all clients by ports
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
		if(command.trim().startsWith(statistic_servercommand5)){
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
	
	/**
	 * This method puts a user in this global chat instance
	 * 
	 * @author jhuhn
	 * @param name
	 *            String representation of the clients username
	 * @param port
	 *            Integer representation of the clients port
	 */
	public void putUser(String name, int port){
		this.clientsByUsername.putIfAbsent(name, port);
	}
	
	/**
	 * @author jhuhn
	 * @return gets the client by username hashmap. The key is the user(String),
	 *         value is the port(Integer)
	 */
	public ConcurrentHashMap<String, Integer> getClientsByUsername() {
		return this.clientsByUsername;
	}
	
	/**
	 * This method removes a user from the global chat
	 * 
	 * @author jhuhn
	 * @param user
	 *            String representation of the user who should get kicked by the
	 *            global chat
	 */
	public void removeUser(String user){
		this.clientsByUsername.remove(user);		
	}
}
