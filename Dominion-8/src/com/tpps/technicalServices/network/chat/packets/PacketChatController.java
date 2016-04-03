package com.tpps.technicalServices.network.chat.packets;

import java.util.ArrayList;

import com.tpps.technicalServices.network.core.packet.Packet;
import com.tpps.technicalServices.network.core.packet.PacketType;

public class PacketChatController extends Packet{

	private static final long serialVersionUID = 1L;
	private String command;
	private ArrayList<String> members;
	private String memberOfChatRoom;
	
	
	public PacketChatController(String command, ArrayList<String> members) {
		super(PacketType.CHAT_CONTROLLER); 
		this.command = command;
		this.members = members;
	}
	
	public PacketChatController(String command, String memberOfChatRoom) {
		super(PacketType.CHAT_CONTROLLER); 
		this.command = command;
		this.memberOfChatRoom = memberOfChatRoom;
	}

	@Override
	public String toString() {
		return "Command: '" + this.command + "' , Member of ChatRoom: '" + this.memberOfChatRoom + "' ,  Members: " + this.members.toString();
	}
	
	public String getCommand() {
		return command;
	}
	
	public ArrayList<String> getMembers() {
		return members;
	}
	
	public String getMemberOfChatRoom() {
		return memberOfChatRoom;
	}

}
