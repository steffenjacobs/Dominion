package com.tpps.application.game.card;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.tpps.application.network.game.ClientGamePacketHandler;
import com.tpps.application.network.game.GameClient;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.ui.GraphicFramework;

/**
 * 
 * @author ladler - Lukas Adler
 * @author nwipfler - Nicolas Wipfler
 */

public class ClientCard extends Card {

	public ClientCard(LinkedHashMap<CardAction, Integer> actions,
			LinkedList<CardType> types, String name, int cost,
			GraphicFramework _parent) {
		super(actions, types, name, cost, _parent);
	}

	private static final long serialVersionUID = 6557548927740502052L;
	
	
	@Override
	public void onMouseClick() {				
		

	}
	
	@Override
	public void onMouseEnter() {
	

	}

	@Override
	public void onMouseExit() {
	

	}
	
	public static void main(String[] args) {
		GameClient g;
		try {
			g = new GameClient(new InetSocketAddress("localhost", 1339), new ClientGamePacketHandler());
			
			g.sendMessage(new PacketPlayCard("Chappel1", "karl"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}