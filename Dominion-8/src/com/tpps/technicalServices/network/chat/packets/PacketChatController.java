package com.tpps.technicalServices.network.chat.packets;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

/**
 * This class represents a packet that will be sent via network
 * 
 * @author jhuhn
 *
 */
public class PacketChatController extends Packet{

	private static final long serialVersionUID = -8336969737252169655L;
	private String command;
	private ArrayList<String> members;
	private String memberOfChatRoom;
	
	private String user;
	private int userport;
	private int chatroomId;
	private HashMap<String, Color> colorMap = new HashMap<String, Color>();
	
	
	/**
	 * constructor to create a chatroom
	 * 
	 * @author jhuhn
	 * @param command
	 *            String representation of the command
	 * @param members
	 *            an ArrayList of all members who want to participate to the
	 *            chatroom
	 */
	public PacketChatController(String command, ArrayList<String> members) {
		super(PacketType.CHAT_CONTROLLER); 
		this.command = command;
		this.members = members;
	}
	
	/**
	 * constructor to delete a chatroom
	 * 
	 * @author jhuhn
	 * @param command
	 *            String representation of the command
	 * @param chatroomId
	 *            ID of a specific chatroom, needed to delete a chatroom
	 */
	public PacketChatController(String command, int chatroomId) {
		super(PacketType.CHAT_CONTROLLER); 
		this.command = command;
		this.chatroomId = chatroomId;
	}
	
	
	/**
	 * constructor to add a user to a chatroom
	 * 
	 * @author jhuhn
	 * @param command
	 *            String representation of the command
	 * @param user
	 *            String representation of the user to get added
	 * @param userport
	 *            users port as an Integer
	 * @param chatroomId
	 *            ID that identifies the chatroom
	 */
	public PacketChatController(String command, String user, int userport, int chatroomId) {
		super(PacketType.CHAT_CONTROLLER); 
		this.command = command;
		this.user = user;
		this.userport = userport;
		this.chatroomId = chatroomId;
	}
	
	/**
	 * constructor to send the chatID
	 * 
	 * @author jhuhn
	 * @param chatRoomId
	 *            Integer that identifies the chatroom
	 * @param colorMap Map of all user colors, used for gamelog
	 */
	public PacketChatController(int chatRoomId, HashMap<String, Color> colorMap) {
		super(PacketType.CHAT_CONTROLLER); 
		this.chatroomId = chatRoomId;
		this.colorMap =  colorMap;
	}

	/**
	 * @see com.tpps.technicalServices.network.core.packet.Packet#toString()
	 */
	@Override
	public String toString() {
		return "Command: '" + this.command + "' , Member of ChatRoom: '" + this.memberOfChatRoom + "' ,  Members: " + this.members.toString();
	}
	
	/**
	 * @author jhuhn
	 * @return a String representation of the chatcommand
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * @author jhuhn
	 * @return an ArrayList of all members (String)
	 */
	public ArrayList<String> getMembers() {
		return members;
	}
	
	/**
	 * @author jhuhn
	 * @return a String representation of one user who participate the chat
	 */
	public String getMemberOfChatRoom() {
		return memberOfChatRoom;
	}
	
	/**
	 * @author jhuhn
	 * @return String representation of the user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * @author jhuhn
	 * @return Integer of the clients port
	 */
	public int getUserport() {
		return userport;
	}
	
	/**
	 * @author jhuhn
	 * @return chatroom id as an Integer
	 */
	public int getChatroomId() {
		return chatroomId;
	}
	
	/**
	 * @return a Hashmap with key user and value Color
	 */
	public HashMap<String, Color> getColorMap() {
		return colorMap;
	}
}
