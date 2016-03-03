package com.tpps.test.application.network;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.tpps.application.game.card.Card;
import com.tpps.application.game.card.CardAction;
import com.tpps.application.game.card.CardType;
import com.tpps.application.network.game.ClientGamePacketHandler;
import com.tpps.application.network.game.GameClient;
import com.tpps.application.network.gameSession.packets.PacketPlayCard;
import com.tpps.technicalServices.util.CollectionsUtil;

public class ServerTest {
	
	public void setUp(){
		GameClient g;
		try {
			
			g = new GameClient(new InetSocketAddress("localhost", 1339), new ClientGamePacketHandler());
			Card card = new Card(CollectionsUtil.linkedHashMapAction(CollectionsUtil.linkedList(CardAction.ADD_ACTION_TO_PLAYER), 
					CollectionsUtil.linkedList(2)), CollectionsUtil.linkedList(CardType.ACTION), "chappel", 20);
			
			g.sendMessage(new PacketPlayCard("Estate0", "karl"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ServerTest().setUp();
	}

}
