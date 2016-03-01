package com.tpps.application.network.game;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.packet.Packet;


public class ClientGamePacketHandler extends PacketHandler{

	@Override
	public void handleReceivedPacket(int port, Packet packet) {	
		ServerConnectionThread requester = parent.getClientThread(port);
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}
		
		
	}

}
