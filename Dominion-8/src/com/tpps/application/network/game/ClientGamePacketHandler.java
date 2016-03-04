package com.tpps.application.network.game;

import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.core.packet.Packet;
import com.tpps.application.network.gameSession.packets.PacketEnableDisable;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;

import com.tpps.application.network.gameSession.packets.PacketSendClientId;



/**
 * 
 * @author ladler - Lukas Adler
 *
 */
public class ClientGamePacketHandler extends PacketHandler{
	private GameClient gameClient;

	@Override
	public void handleReceivedPacket(int port, Packet packet) {	
		
		if (packet == null) {
			super.output("<- Empty Packet from (" + port + ")");
			return;
		}
		switch (packet.getType()) {
			case CARD_PLAYED:
				System.out.println("packet received from Server of type " + packet.getType() + "id: " + ((PacketPlayCard)packet).getCardID());				
			break;
			case SEND_CLIENT_ID:
				this.gameClient.setClientId(((PacketSendClientId)packet).getClientId());					
			break;
			case CLIENT_SHOULD_DISCONECT:
				this.gameClient.disconnect();
			break;
			case ENABLE_DISABLE:
				if (((PacketEnableDisable)packet).getClientId() == this.gameClient.getClientId()){
//					gameGui.setEnabled();
					System.out.println("my gameWindow is enabled");
				}
				else{
					System.out.println("my gameWindo is disabled");
				}
				
				
			break;
			default:
				System.out.println("unknown packed type");
			break;
				
		}
		
		
		
	}

	public void setGameClient(GameClient gameClient) {
		this.gameClient = gameClient;
	}
	
	
	
	

}
