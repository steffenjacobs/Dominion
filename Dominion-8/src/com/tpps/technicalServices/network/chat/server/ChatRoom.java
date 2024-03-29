package com.tpps.technicalServices.network.chat.server;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.tpps.technicalServices.network.chat.packets.PacketChatVote;
import com.tpps.technicalServices.network.chat.packets.PacketSendAnswer;
import com.tpps.technicalServices.network.chat.packets.PacketSendChatAll;
import com.tpps.technicalServices.network.chat.packets.PacketSendChatCommand;
import com.tpps.technicalServices.network.chat.packets.PacketSendChatToClient;
import com.tpps.technicalServices.network.login.SQLHandling.SQLHandler;
import com.tpps.technicalServices.network.login.SQLHandling.SQLStatisticsHandler;

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
	
	private final static String help_servercommand1 = "help";
	private final static String clients_servercommand2 = "show all clients";
	private final static String ports_servercommand3 = "show all ports";
	private final static String clientsAndPort_servercommand4 = "show all clients by ports";
	private final static String votekick_servercommand5 = "votekick <nickname>";
	private final static String vote_servercommand6 = "vote [y/n] only use in a active vote";
	private final static String votekickresult_servercommand7 = "show votekickresults";
	private final static String statistics_servercommand8 = "show all statistics";
	
	private String votekickresults;	
	private Votekick votekick;
	private ColorPool pool;
	
	private int gameserverPort;
	
	/**
	 * initializes the ChatRoom object
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param clientsByUser
	 *            a ConcuttentHashMap that handle all clients and ports for this
	 *            chatroom object
	 * @param server
	 *            the serverobject which is important to send packets
	 * @param chatpackethandler
	 *            the chatpackethandler object, that receives all packets
	 */
	public ChatRoom(ConcurrentHashMap<String, Integer> clientsByUser, ChatServer server, ChatPacketHandler chatpackethandler){
		this.clientsByUsername = clientsByUser;
		this.server = server;
		this.id = idcounter++;
		this.chatpackethandler = chatpackethandler;
		this.pool = chatpackethandler.getPool();
	}
	
	/**
	 * This method sends a packet to all clients in a chatroom except the user
	 * that sent the packet, used for public chat in chatroom
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            a packet that received the server from a user (public chat)
	 */
	public void sendChatToAllExceptSender(PacketSendChatAll packet){
	//	String message = packet.getChatmessage();
		String sender = packet.getUsername();
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
		    String nickname = entry.getKey();
		    if(nickname.equals(sender)){
		    	continue;
		    }
		    int port = entry.getValue();
		    PacketSendAnswer answer = new PacketSendAnswer(
					ChatServer.sdf.format(new Date()),
					packet.getUsername(), packet.getChatmessage(),
					pool.getUserColor(packet.getUsername()));
		    try {
				server.sendMessage(port, answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * this method either counts a vote from a user or sends a message back that
	 * he already voited
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            the packet that received to cast a vote
	 */
	public void handleVote(PacketChatVote packet){
		if(votekick.getNotvotedyet().contains(packet.getSender())){
			votekick.getNotvotedyet().add(packet.getSender());
			votekick.addVote(packet.getSender(), packet.getVoted());
		}else{
			PacketSendAnswer answer = new PacketSendAnswer("", "BOT", "You voted already", ColorPool.commandAndErrorColor);
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method is responsible to send a private message to a client
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            the packet that received the server. it contains a private
	 *            message
	 */
	public void sendChatToChatRoomClient(PacketSendChatToClient packet){
		String sender = packet.getSender();
		String receiver = packet.getReceiver().trim();
		String message = packet.getMessage();
		
		if(!this.clientsByUsername.containsKey(receiver)){
			PacketSendAnswer answer = new PacketSendAnswer(
					ChatServer.sdf.format(new Date().getTime())
							+ "No such a user online: ", receiver, "",
					ColorPool.commandAndErrorColor);
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			return;
		}
		
		int port = this.clientsByUsername.get(receiver);
		PacketSendAnswer answer = new PacketSendAnswer(
				ChatServer.sdf.format(new Date()) + "PM from User: ", sender,
				message, this.pool.getUserColor(sender));
		try {
			server.sendMessage(port, answer);
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is responsible to send a message back to a client
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param sender
	 *            a String representation of the nickname who sent the message
	 * @param answer
	 *            the packet which receive the receiver (including the message)
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
	 * This method evaluates commands and passes to the right method
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            the packet that received the server from a client
	 */
	public void evaluateCommand(PacketSendChatCommand packet){		
		if(packet.getChatcommand().startsWith("votekick ")){
			if(this.votekick != null){			
				PacketSendAnswer answer6 = new PacketSendAnswer("", "BOT", "There is an active vote currently", ColorPool.commandAndErrorColor);
				try {
					this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer6);
				} catch (IOException e) {				
					e.printStackTrace();
				}
			}else{
				this.setupVotekick(packet);									
			}
			return;
		}else if(packet.getChatcommand().startsWith("vote ")){
			this.voteForVotekickCommand(packet);
			return;
		}
		
		switch(packet.getChatcommand()){
		case help_servercommand1:
			System.out.println("go to chatcmd1 <=> help command");
			this.evaluateHelpCommand(packet);
			break;
		case clients_servercommand2:
			this.evaluateShowAllClientsCommand(packet);
			break;
		case ports_servercommand3:
			this.evaluateShowAllPortsCommand(packet);
			break;
		case clientsAndPort_servercommand4: 
			this.evaluateClientAndPortsCommand(packet);
			break;
		case votekickresult_servercommand7:
			this.evaluateShowVotekickResulutsCommand(packet);
			break;
		case statistics_servercommand8:
			this.evaluateStatisticsCommand(packet);
			break;
		default:
			PacketSendAnswer answer5 = new PacketSendAnswer("", "BOT",
					"unknown command: " + packet.getChatcommand(),
					ColorPool.commandAndErrorColor);
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer5);
			} catch (IOException e) {			
				e.printStackTrace();
			}
			break;
		}		
	}
	
	/**
	 * This method is called when a user types in chat: '/show all statistics'.
	 * This method gets all statistics from the database from all users in the
	 * chatroom
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            the packet that received from the user
	 */
	private void evaluateStatisticsCommand(PacketSendChatCommand packet){
		SQLHandler.init();
		SQLHandler.connect();
	
		String result = "";
		String line = "";
		for (Entry<String, Integer> entry : clientsByUsername.entrySet()) {
			String nextMember = entry.getKey();
			try {
				ResultSet rs = SQLStatisticsHandler.getStatisticsForPlayer(nextMember.trim());
				rs.next();
				String wins = "" + rs.getInt("wins");
				String losses = "" + rs.getInt("losses");
				String ratio = "" + rs.getDouble("win_loss");
				String totalMatches = "" + rs.getInt("games_played");
				String rank = "" + rs.getInt("rank");
				line = nextMember + ": \n" + "	wins: " + wins + "\n	"
						+ "losses: " + losses + "\n	" + "ratio: " + ratio
						+ "\n	" + "total matches: " + totalMatches + "\n	"
						+ "rank: " + rank + "\n-------------------------\n";
				result += line;
				System.out.println(line);
			} catch (SQLException e) {
				e.printStackTrace();
				continue;				
			}
		}
		PacketSendAnswer answer5 = new PacketSendAnswer("", "BOT",
				"ALL STATISTICS IN ROOM: \n" + result,
				ColorPool.commandAndErrorColor);
		try {
			this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer5);
		} catch (IOException e) {			
			e.printStackTrace();
		}	
		SQLHandler.closeConnection();
	}
	
	/**
	 * This method is called when a user types in chat: '/help'. This method
	 * sends all chatcommands as a String back to the user
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            the packet that received the server from a client
	 */
	private void evaluateHelpCommand(PacketSendChatCommand packet){
		String msg = "Commands: \n/" + help_servercommand1 + "\n/"
				+ clients_servercommand2 + "\n/" + ports_servercommand3 + "\n/"
				+ clientsAndPort_servercommand4 + "\n/" + votekick_servercommand5 + "\n/"
				+ vote_servercommand6 + "\n/" + votekickresult_servercommand7 + "\n/"
				+ statistics_servercommand8 + "\n" + "@<user> <PM>";
//		PacketSendAnswer answer = new PacketSendAnswer(msg);
		PacketSendAnswer answer = new PacketSendAnswer("", "BOT", msg, ColorPool.commandAndErrorColor);
		try {
			this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is called when a user types in chat: '/show all clients'. This
	 * method put all users from this chatroom in a String and sends it back
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            the packet that received the server from a client
	 */
	private void evaluateShowAllClientsCommand(PacketSendChatCommand packet){
		Enumeration<String> clients = this.clientsByUsername.keys();
		String msg2 = "All connected Clients in this chatroom: \n";
		while(clients.hasMoreElements()){
			msg2 += clients.nextElement() + "\n";
		}
		PacketSendAnswer answer2 = new PacketSendAnswer("", "BOT", msg2, ColorPool.commandAndErrorColor);
		try {
			this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer2);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is called when a user types in chat: '/show all ports'. This
	 * method put all ports from this chatroom in a String and sends it back
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet the packet that received the server from a client
	 */
	private void evaluateShowAllPortsCommand(PacketSendChatCommand packet){
		Enumeration<Integer> ports = this.clientsByUsername.elements();
		String msg3 = "All connected ports in this chatroom: \n";
		while(ports.hasMoreElements()){
			msg3 += ports.nextElement() + "\n";
		}
		PacketSendAnswer answer3 = new PacketSendAnswer("", "BOT", msg3, ColorPool.commandAndErrorColor);
		try {
			this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer3);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * This method is called when a user types in chat: '/show all clients b ports'. This
	 * method put all users/ports from this chatroom in a String and sends it back.
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet the packet that received the server from a client
	 */
	private void evaluateClientAndPortsCommand(PacketSendChatCommand packet){
		Enumeration<Integer> ports2 = this.clientsByUsername.elements();
		Enumeration<String> clients2 = this.clientsByUsername.keys();
		String msg4 = "All connected clients with ports in this chatroom: \n";
		while(ports2.hasMoreElements()){
			msg4 += clients2.nextElement() + "  :  " + ports2.nextElement() + "\n";
		}
		PacketSendAnswer answer4 = new PacketSendAnswer("", "BOT", msg4, ColorPool.commandAndErrorColor);
		try {
			this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer4);
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * This mehtod is called when a user calls the /votekick command correctly.
	 * It is used to set up a votekick object
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            the packet that received the server from a client
	 */
	private void setupVotekick(PacketSendChatCommand packet){
		try{	//sets up the votekick
			String[] words = packet.getChatcommand().split("\\s+");
			ArrayList<String> notvoted = this.getClients();
			notvoted.remove(packet.getSender());
			
			if(!clientsByUsername.containsKey(words[1])){
				this.sendChatToChatRoomClient(
						packet.getSender(),
						new PacketSendAnswer(
								"",
								"BOT",
								"Client doesn't exist in chatroom:_" + words[1],
								ColorPool.commandAndErrorColor));
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
			PacketSendAnswer answer7 = new PacketSendAnswer("", "BOT",
					"unknown command: " + packet.getChatcommand(),
					ColorPool.commandAndErrorColor);
			try {
				this.server.sendMessage(this.clientsByUsername.get(packet.getSender()), answer7);
			} catch (IOException e1) {					
				e1.printStackTrace();
			}
		}

	}

	/**
	 * evaluate the '/vote ' command. It is used to vote for a votekick.
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            the packet that received the server from a client
	 */
	private void voteForVotekickCommand(PacketSendChatCommand packet){
		if(this.votekick == null){
			PacketSendAnswer answer = new PacketSendAnswer("", "BOT", "There is currently no vote", ColorPool.commandAndErrorColor);
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
			return;
		}		
		
		if(this.votekick.checkIfUserVoted(packet.getSender())){
			PacketSendAnswer answer = new PacketSendAnswer("", "BOT", "You voted already", ColorPool.commandAndErrorColor);
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
			return;
		}
		
		if(packet.getChatcommand().startsWith("vote y")){
			this.votekick.addVote(packet.getSender(), true);
			PacketSendAnswer answer = new PacketSendAnswer("", "BOT", "You voted successfully", ColorPool.commandAndErrorColor);
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}else if(packet.getChatcommand().startsWith("vote n")){
			this.votekick.addVote(packet.getSender(), false);
			PacketSendAnswer answer = new PacketSendAnswer("", "BOT", "You voted successfully", ColorPool.commandAndErrorColor);
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}else{
			PacketSendAnswer answer = new PacketSendAnswer("", "BOT", "Your vote command failed, [y/n] is avaible", ColorPool.commandAndErrorColor);
			String sender = packet.getSender();
			this.sendChatToChatRoomClient(sender, answer);
		}
	}
	
	/**
	 * executes the 'show votekickresults' command. This method gets all results
	 * and send it back to the user
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param packet
	 *            the packet that received the server from a client
	 */
	private void evaluateShowVotekickResulutsCommand(PacketSendChatCommand packet){
		if(this.votekickresults == null){
			PacketSendAnswer answerx = new PacketSendAnswer("", "BOT", "There are no votekick results", ColorPool.commandAndErrorColor);
			this.sendChatToChatRoomClient(packet.getSender(), answerx);
		}else{
			PacketSendAnswer answerx = new PacketSendAnswer("", "BOT", this.votekickresults, ColorPool.commandAndErrorColor);
			this.sendChatToChatRoomClient(packet.getSender(), answerx);
		}
	}
	
	/**
	 * This methods sends a message to all clients in the chatroom
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param msg
	 *            a String representation of text message to send
	 */
	public void sendMessageToAll(String msg){
		PacketSendAnswer answer = new PacketSendAnswer("", "BOT", msg, ColorPool.commandAndErrorColor);
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
	 * 
	 * @author jhuhn - Johannes Huhn
	 */
	private void evaluateVotekick(){
		String getkicked = this.votekick.getUsertogetkicked();
		if(this.votekick.fastEvaluateVote()){			
			this.chatpackethandler.kickPlayer(getkicked);
			this.sendMessageToAll("The player '" + getkicked + "' gets kicked!");
			
			//SEND PACKET TO GAMESERVER TO VOTEKICK USER
			new VotekickClient(this.gameserverPort).sendVotekickPacket(getkicked);
			System.out.println("kicked from chatroom " + id + " GameServerPort: " + this.gameserverPort);
		}else{
			this.sendMessageToAll("The player '" + getkicked + "' stays in the match!");
		}
		this.votekickresults = this.votekick.printResults();
		System.out.println(this.votekickresults);
		ChatRoom.this.votekick = null; 		
	}
	
	/**
	 * gets all clients in the chatroom
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @return an Arraylist of Strings with all clients that are connected in
	 *         the chatroom
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
	 * @author jhuhn - Johannes Huhn
	 * @return the chatroom id as an Integer
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * overrides the toString() method to get a useful String representation of the chatroom
	 * 
	 * @author jhuhn - Johannes Huhn
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
	 * @author jhuhn - Johannes Huhn
	 * @return the ConcurrentHashMap included all clients by its ports Key:
	 *         String: username and Object: Integer: port
	 */
	public ConcurrentHashMap<String, Integer> getClientsByUsername() {
		return clientsByUsername;
	}
	
	/**
	 * This method removes a user from this chatroom. It is called e.g. when a
	 * user disconnects from the server
	 * 
	 * @author jhuhn - Johannes Huhn
	 * @param user
	 *            a String representation of the users nickname
	 */
	public void removeUser(String user){
		this.clientsByUsername.remove(user);		
	}
	
	
	/**
	 * @author jhuhn - Johannes Huhn
	 * @param gameserverPort
	 *            port of the gameserver, that is connected with this chatromm
	 *            instance
	 */
	public void setGameserverPort(int gameserverPort) {
		System.out.println("set the gameServer port");
		this.gameserverPort = gameserverPort;
	}
}
