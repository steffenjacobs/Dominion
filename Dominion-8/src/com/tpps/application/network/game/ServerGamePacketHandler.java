package com.tpps.application.network.game;

import java.io.IOException;

import com.tpps.application.game.card.CardType;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.ServerConnectionThread;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.packet.Packet;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class ServerGamePacketHandler extends PacketHandler{
	GameServer server;

	public GameServer getServer() {
		return server;
	}

	public void setServer(GameServer server) {
		this.server = server;
	}

	@Override
	public void handleReceivedPacket(int port, Packet packet) {		
		ServerConnectionThread requester = parent.getClientThread(port);
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}
		switch (packet.getType()) {
			case CARD_PLAYED:
				System.out.println("packet received from Client of type " + packet.getType() + 
						" card is of Type " + ((PacketPlayCard)packet).getCardType());
			
				try {					
					server.sendMessage(port, new PacketPlayCard(2, "anna", CardType.DUCHY));
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				
				
				
				
				
			break;
			default:
				System.out.println("unknown packed type");
			break;
				
		}
		
	}

}
