package com.tpps.application.network.game;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.application.network.packet.Packet;

/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class ClientGamePacketHandler extends PacketHandler{

	@Override
	public void handleReceivedPacket(int port, Packet packet) {	
		
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}
		switch (packet.getType()) {
			case CARD_PLAYED:
				System.out.println("packet received from Server of type " + packet.getType() + 
						" card is of Type " + ((PacketPlayCard)packet).getCardType());				
			break;
			default:
				System.out.println("unknown packed type");
			break;
				
		}
		
		
		
	}

}
