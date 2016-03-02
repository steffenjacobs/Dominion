package com.tpps.application.network.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import com.tpps.application.game.card.CardType;
import com.tpps.application.network.core.Client;
import com.tpps.application.network.core.PacketHandler;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;

public class GameClient extends Client{

	public GameClient(SocketAddress _address, PacketHandler _handler) throws IOException {
		super(_address, _handler, false);
		
	}
	
	public static void main(String[] args) {
		try {
			
			GameClient g = new GameClient(new InetSocketAddress("localhost", 1339), new ClientGamePacketHandler());			
			g.sendMessage(new PacketPlayCard(2, "karl", CardType.COPPER));
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
