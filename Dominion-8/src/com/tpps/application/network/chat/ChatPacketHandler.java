package com.tpps.application.network.chat;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;

public class ChatPacketHandler extends PacketHandler{

	private ChatServer server;
	
	@Override
	public void handleReceivedPacket(int port, Packet packet) {
		// TODO Auto-generated method stub
		
	}

	public ChatServer getServer() {
		return server;
	}

	public void setServer(ChatServer server) {
		this.server = server;
	}

}
